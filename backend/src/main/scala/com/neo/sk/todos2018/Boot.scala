package com.neo.sk.todos2018


import akka.actor.{ActorSystem, Props}
import akka.dispatch.MessageDispatcher
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.util.Timeout
import akka.actor.typed.scaladsl.adapter._
import com.neo.sk.todos2018.service.HttpService
import com.neo.sk.todos2018.coreblog._

import scala.language.postfixOps
import scala.sys.Prop
import scala.util.{Failure, Success}
import java.util.Timer
import java.util.TimerTask
import java.util.Date

/**
  * User: Taoz
  * Date: 11/16/2016
  * Time: 1:00 AM
  */
object Boot extends HttpService {


  import concurrent.duration._
  import com.neo.sk.todos2018.common.AppSettings._
	

  override implicit val system = ActorSystem("appSystem", config)
  // the executor should not be the default dispatcher.
  override implicit val executor: MessageDispatcher =
    system.dispatchers.lookup("akka.actor.my-blocking-dispatcher")

  override implicit val materializer = ActorMaterializer()

  override implicit val timeout = Timeout(20 seconds) // for actor asks

  override implicit val scheduler = system.scheduler

  val log: LoggingAdapter = Logging(system, getClass)
  val urlFollow = "https://weibo.cn/5634035539/follow"
  val urlHome = "https://weibo.cn/5634035539"
  val urlFans = "https://weibo.cn/5634035539/fans"
  val urlInfo = "https://weibo.cn/5634035539/info"
  val urlMyArticle = "https://weibo.cn/5634035539/profile"

  val commentActor = system.spawn(CommentActor.behavior, name="comment")
  val articleActor = system.spawn(ArticleActor.init(urlMyArticle, commentActor), name = "article")
  val infoActor = system.spawn(InfoActor.init(urlInfo), name="info")
  val fansActor = system.spawn(FansActor.init(urlFans, urlHome), name = "fans")
	val followActor = system.spawn(FollowActor.init(urlFollow, urlHome), name="follow")
  val spiderActor = system.spawn(Spider.init(urlHome, followActor, fansActor, infoActor, articleActor), name = "spider")
  //val s1 = system.

  def main(args: Array[String]) {
    log.info("Starting.")
    val binding = Http().bindAndHandle(routes, httpInterface, httpPort)
    binding.onComplete {
      case Success(b) ⇒
        val localAddress = b.localAddress
        println(s"Server is listening on ${localAddress.getHostName}:${localAddress.getPort}")
	      //val handlerActor = system.actorOf(Props.defaultCreator, "")
//        println(s"Server is listening on http://localhost:${localAddress.getPort}/todos2018/index")
      case Failure(e) ⇒
        println(s"Binding failed with ${e.getMessage}")
        system.terminate()
        System.exit(-1)
    }
  }
}
