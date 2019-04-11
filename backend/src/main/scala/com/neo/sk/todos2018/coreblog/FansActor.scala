package com.neo.sk.todos2018.coreblog

import akka.actor.ActorSystem
import akka.actor.typed.scaladsl.{ActorContext, Behaviors, StashBuffer, TimerScheduler}
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.scaladsl.adapter._
import com.neo.sk.todos2018.coreblog.Spider.{SpiderCommand, WaitAMin}
import org.slf4j.LoggerFactory
import temp.douban.crawl

import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.mutable
import concurrent.duration._
import scala.concurrent.Future
import scala.util.{Failure, Random, Success}
import com.neo.sk.todos2018.Boot.spiderActor

object FansActor {
  private val log = LoggerFactory.getLogger(this.getClass)

  sealed trait FansCommand

  private case object TimeOutMsg extends FansCommand
  case object StartWork extends FansCommand
  case object WorkOtherPage extends FansCommand

  case class GetRemainingPageUrl(url: String, page: Int) extends FansCommand
  case class FetchUrl(url: String) extends FansCommand

  def init(url: String):Behavior[FansCommand] = {
    Behaviors.setup[FansCommand]{ctx =>
      log.info(s"${ctx.self.path} start work")
      Behaviors.withTimers[FansCommand] { implicit timer =>
        timer.startSingleTimer(TimeOutMsg, StartWork, 5.seconds)
        val hash: mutable.Queue[String] = mutable.Queue()
        idle(url, hash)
      }
    }
  }


  //分页功能

  def idle(url: String, hash: mutable.Queue[String]): Behavior[FansCommand] = {
    Behaviors.receive[FansCommand]{(ctx, msg)=>
      msg match {
        case StartWork =>
          crawl.fetch(url).onComplete{t=>
            val html = t.toString
            if(html.length < 10){
              Thread.sleep(Random.nextInt(15) * 1000 + 10000)
              ctx.self ! StartWork
            }else{
              val page = crawl.getPage(html)
              val urlList = crawl.parseFollow(html)
              spiderActor ! Spider.GetUrlFromFans(urlList)
              ctx.self ! GetRemainingPageUrl(url, page)
            }
          }
          Behaviors.same

        case FetchUrl(url) =>
          crawl.fetch(url).onComplete{t=>
            val html = t.toString
            if(html.length < 10){
              log.error(s"========get url $url error!,重新请求============")
              spiderActor ! WaitAMin
              Thread.sleep(Random.nextInt(15) * 1000 + 10000)
              ctx.self ! FetchUrl(url)
            }else {
              val urlList = crawl.parseFollow(html)
              spiderActor ! Spider.GetUrlFromFans(urlList)
              val page = crawl.getPage(html)//解析第一页数据
              ctx.self ! GetRemainingPageUrl(url, page)
            }
          }
          Behaviors.same

        case GetRemainingPageUrl(url, page) =>
          if(page > 1){
            val pages = if(page > 100) 100 else page
            for(i<- 2 to pages){
              val urlPage = url + s"?page=${i}"
              hash.enqueue(urlPage)
            }
            ctx.self ! WorkOtherPage
          }
          Behaviors.same

        case WorkOtherPage =>
          if(hash.length > 0){
            val urlTemp = hash.dequeue()
            crawl.fetch(urlTemp).onComplete{html=>
              if(html.toString.length < 10){
                log.error(s"get url $urlTemp error!,重新放入队列")
                hash.enqueue(urlTemp)
              }else{
                val urlList = crawl.parseFollow(html.toString)
                spiderActor ! Spider.GetUrlFromFans(urlList)
              }
            }
            if(hash.length > 0){
              Thread.sleep(Random.nextInt(6)*1000 + 5000)
              ctx.self ! WorkOtherPage
            }
          }
          Behaviors.same
      }
    }
  }
}
