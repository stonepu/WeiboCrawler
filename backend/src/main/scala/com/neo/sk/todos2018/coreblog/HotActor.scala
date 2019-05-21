package com.neo.sk.todos2018.coreblog

import akka.actor.ActorSystem
import akka.actor.typed.scaladsl.{ActorContext, Behaviors, StashBuffer, TimerScheduler}
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.scaladsl.adapter._
import com.neo.sk.todos2018.coreblog.Spider.SpiderCommand
import org.slf4j.LoggerFactory
import temp.douban.crawl

import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.mutable
import concurrent.duration._
import scala.concurrent.Future
import scala.util.{Failure, Random, Success}
import com.neo.sk.todos2018.Boot.spiderActor
import com.neo.sk.todos2018.models.dao.BlogDao.realtimehotDao

import scala.collection.mutable.ListBuffer

object HotActor {
  private val log = LoggerFactory.getLogger(this.getClass)
  sealed trait HotCommand

  case object TimeOutMsg extends HotCommand
  case object StartWork extends HotCommand
  case object Fetch extends HotCommand

  def init(url: String): Behavior[HotCommand] = {
    Behaviors.setup[HotCommand]{ctx =>
      log.info(s"${ctx.self.path} start work")
      Behaviors.withTimers[HotCommand]{ implicit timer =>
        timer.startSingleTimer(TimeOutMsg, StartWork, 5.seconds)
        idle(url)
      }
    }
  }

  def idle(url: String)(implicit timer: TimerScheduler[HotCommand]): Behavior[HotCommand] = {
    Behaviors.receive[HotCommand]{(ctx, msg) =>
      msg match {
        case StartWork =>
          //ctx.self ! Fetch
          //timer.startPeriodicTimer(TimeOutMsg, Fetch, 1.minutes)
          Behaviors.same

        case Fetch =>
          realtimehotDao.dtl()
          println(s"=====hot working: url= ${url} =====")
          crawl.fetch(url).onComplete{t =>
            val html = t.get
            if(html.length>10){
              crawl.parseHot(html)
            }
          }
          Behaviors.same
      }
    }
  }
}
