package com.neo.sk.todos2018.coreblog

import akka.actor.ActorSystem
import akka.actor.typed.scaladsl.{ActorContext, Behaviors, StashBuffer, TimerScheduler}
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.scaladsl.adapter._
import com.neo.sk.todos2018.common.AppSettings
import com.neo.sk.todos2018.coreblog.ArticleActor.{ArticleCommand, FetchUrl}
import com.neo.sk.todos2018.coreblog.FansActor.FansCommand
import com.neo.sk.todos2018.coreblog.FollowActor.FollowCommand
import org.slf4j.LoggerFactory
import temp.douban.crawl

import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.mutable
import concurrent.duration._
import scala.concurrent.Future
import scala.util.{Failure, Random, Success}
import com.neo.sk.todos2018.coreblog.FollowActor
import com.neo.sk.todos2018.coreblog.InfoActor.InfoCommand
import com.neo.sk.todos2018.models.dao.BlogDao.UrlSaveDao


object Spider {
  private val log = LoggerFactory.getLogger(this.getClass)
  sealed trait SpiderCommand

  private case object TimerKey
  private case object TimeOutMsg extends SpiderCommand


  final case object StartWork extends SpiderCommand
  final case object UpdateUrl extends SpiderCommand
  final case object FetchTask extends SpiderCommand
  final case object FinishTask extends SpiderCommand

  case class parseMyHome(html: String) extends SpiderCommand

  case object WaitAMin extends SpiderCommand
  case class GetUrlFromFol(urlList: List[String]) extends SpiderCommand
  case class GetUrlFromFans(urlList: List[String]) extends SpiderCommand

  case class Distribution(url: String) extends SpiderCommand
  case class FailedUrl(url: String) extends SpiderCommand

  var needStop: Boolean = false

  final case class ChildDead(childName: String) extends SpiderCommand
  object urlType extends Enumeration{
    val info, article, comment, firstArticle , follow, fans= Value
  }

  def init(urlList: String,
           follow: ActorRef[FollowCommand],
           fans: ActorRef[FansCommand],
           info: ActorRef[InfoCommand],
           articleActor: ActorRef[ArticleCommand]): Behavior[SpiderCommand] = {
    Behaviors.setup[SpiderCommand]{ ctx =>
      implicit val stashBuffer = StashBuffer[SpiderCommand](Int.MaxValue)
      log.info(s"${ctx.self.path} is starting")
      Behaviors.withTimers[SpiderCommand]{ implicit timer =>
        timer.startSingleTimer(TimeOutMsg, StartWork, 5.seconds)
        val hashUrl2Get: mutable.Queue[String] = mutable.Queue()
        val hashSet: mutable.HashSet[String] = mutable.HashSet()

        idle(urlList,hashUrl2Get,follow, fans, info, articleActor, hashSet)
      }
    }
  }

  def idle(url: String,
           hashUrl2Get: mutable.Queue[String],
           follow: ActorRef[FollowCommand],
           fans: ActorRef[FansCommand],
           info: ActorRef[InfoCommand],
           articleActor: ActorRef[ArticleCommand],
           hashSet: mutable.HashSet[String])(implicit stashBuffer: StashBuffer[SpiderCommand], timer: TimerScheduler[SpiderCommand]): Behavior[SpiderCommand] = {
    Behaviors.receive[SpiderCommand]{ (ctx, msg) =>
      msg match {
        case StartWork =>
          UrlSaveDao.getUrl().onComplete{t =>
            val urlList = t.get.toList
            if(urlList.isEmpty){
              var str = "www."
              hashUrl2Get.enqueue(url)
              ctx.self ! FetchTask
              /*crawl.fetch(url).onComplete{t =>
                str = t.toString
                ctx.self ! parseMyHome(str)
              }*/
            }else{
              urlList.foreach(p=> hashUrl2Get.enqueue(p))
              ctx.self ! FetchTask
            }
          }
          timer.startPeriodicTimer(TimerKey, UpdateUrl, 1.minutes)
          timer.startPeriodicTimer(TimerKey, FetchTask, 3.seconds)
          Behaviors.same

        case UpdateUrl=>
          UrlSaveDao.delete()
          hashUrl2Get.foreach(t=> UrlSaveDao.updateUrl(t))
          Behaviors.same

        case parseMyHome(html) =>
          val urlList = crawl.parseMyHome(html)
          //ctx.self ! parseFollow(urlList)
          Behaviors.same

        //和follow的交互
        case GetUrlFromFol(urlList) =>
          urlList.foreach{url =>
            hashUrl2Get.enqueue(url)
          }
          Behaviors.same

        case GetUrlFromFans(urlList) =>
          urlList.foreach{url =>
            hashUrl2Get.enqueue(url)
          }
          Behaviors.same

        case FetchTask =>
          if(hashUrl2Get.length > 0){
            var nextUrl = hashUrl2Get.dequeue()
            while(hashSet.contains(nextUrl)){
              if(hashUrl2Get.length>0){
                nextUrl = hashUrl2Get.dequeue()
              }
            }
            if(!hashSet.contains(nextUrl)){
              hashSet += nextUrl
              ctx.self ! Distribution(nextUrl)
            }
          }
          Behaviors.same

        case WaitAMin =>
          log.debug("you have to wait for some time")
          needStop = true
          Behaviors.same

        case Distribution(url) =>
          if(url != AppSettings.homeUrl)
            articleActor ! FetchUrl(url)
          crawl.fetch(url).onComplete{t =>
            val html = t.get
            if(html.length < 10){
              Thread.sleep(Random.nextInt(6)*1000 + 4000)
              ctx.self ! Distribution(url)
            }else{
              val isBupt = if(html.contains("北邮") || html.contains("北京邮电大学") || html.contains("BUPT")) true else false
              val urlMap = crawl.get4Url(html)
              if(needStop == true) {
                Thread.sleep(20000)
                needStop = false
              }
              //fixme here 给各个actor发url
              Thread.sleep(Random.nextInt(4)*1000+2000)
              follow ! FollowActor.FetchUrl(urlMap("follow"), url)
              //Thread.sleep(Random.nextInt(6)*1000+2000)
              info ! InfoActor.FetchUrl(urlMap("info"))
              //println(urlMap("info")+" infoUrl start work!")
              Thread.sleep(Random.nextInt(4)*1000+2000)
              fans ! FansActor.FetchUrl(urlMap("fans"), url, isBupt)
            }
          }
          Behaviors.same

        /*case FailedUrl(url) =>
          hashUrl2Get.enqueue(url)
          if(hashSet.contains(url)) hashSet-=url
          ctx.self ! WaitAMin
          Behaviors.same*/

        case ChildDead(childName) =>
          log.warn(s"${ctx.self.path} child=${childName} die ..")
          Behaviors.same

        case unknow =>
          log.error(s"${ctx.self.path} recv an unknow msg=${msg}")
          Behaviors.ignore

      }

    }
  }


  /*private def distributeFollow(ctx: ActorContext[SpiderCommand],
                               followActor: ActorRef[FollowActor.FollowCommand],
                             url: String): ActorRef[UrlDistribution.Command] = {
    val childName = s"待定"
    ctx.child(childName).getOrElse{
      val actor = ctx.spawn(UrlDistribution.init(url, followActor), childName)
      ctx.watchWith(actor, ChildDead(childName))
      actor
    }.upcast[UrlDistribution.Command]
  }*/

  /*def main(args: Array[String]): Unit = {
    val system = ActorSystem("follow")
    val url = "https://weibo.cn"
    val urlFollow = ""
    //val distribution = system.spawn(UrlDistribution.init(), name = "distribution")
    val follows = system.spawn(FollowActor.init(urlFollow,spider), name = "follow")

    val spider = system.spawn(Spider.init(url, follows), name = "spider")
  }*/


}
