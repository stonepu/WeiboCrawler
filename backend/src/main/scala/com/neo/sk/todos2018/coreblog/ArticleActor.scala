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

object ArticleActor {
  private val log = LoggerFactory.getLogger(this.getClass)
  sealed trait ArticleCommand

  private case object TimeOutMsg extends ArticleCommand


  case object StartWork extends ArticleCommand
  case object WorkOtherPage extends ArticleCommand
  case class ParseHtml( html: String) extends ArticleCommand
  case class FetchUrl(url: String) extends  ArticleCommand
  case class GetRemainingPageUrl(url: String, page: Int) extends ArticleCommand

  val behavior: Behavior[ArticleCommand] = {
    Behaviors.setup[ArticleCommand]{ctx =>
      Behaviors.withTimers{implicit timer =>
        timer.startSingleTimer(TimeOutMsg, StartWork, 5.seconds)
        val hash: mutable.Queue[String] = mutable.Queue()
        idle(hash)
      }
    }
  }

  def idle(hash: mutable.Queue[String]): Behavior[ArticleCommand] = {
    Behaviors.receive[ArticleCommand]{(ctx, msg) =>
      msg match {
        case StartWork =>
          Behaviors.same

        case FetchUrl(url) =>
          crawl.fetch(url).onComplete{t =>
            val html = t.toString
            if(html.length < 10) {
              log.error(s"=======get url $url error!,重新请求===========")
              spiderActor ! WaitAMin
              Thread.sleep(Random.nextInt(15) * 1000 + 10000)
              ctx.self ! FetchUrl(url)
            }else{
              //fixme 解析，数据库存储
              crawl.parseAtcl(url, html)
              val page = crawl.getPage(html)
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
          if(hash.length>0){
            val urlTemp = hash.dequeue()
            crawl.fetch(urlTemp).onComplete{html=>
              if(html.toString.length < 10){
                log.error(s"======get url $urlTemp error!,重新放入队列=======")
                hash.enqueue(urlTemp)
              }else{
                crawl.parseAtcl(urlTemp, html.toString)
              }
            }
            if(hash.length>0){
              Thread.sleep(Random.nextInt(6)*1000 + 5000)
              ctx.self ! WorkOtherPage
            }
          }
          Behaviors.same
      }
    }
  }
}
