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

object InfoActor {
  private val log = LoggerFactory.getLogger(this.getClass)
  sealed trait InfoCommand

  private case object TimeOutMsg extends InfoCommand

  case object StartWork extends InfoCommand
  case class ParseHtml(html: String) extends InfoCommand
  case class FetchUrl(url: String) extends InfoCommand

  def init(url: String): Behavior[InfoCommand] = {
    Behaviors.setup[InfoCommand]{ctx =>
      Behaviors.withTimers[InfoCommand]{implicit timer =>
        timer.startSingleTimer(TimeOutMsg, StartWork, 5.seconds)
        idle(url)
      }
    }
  }

  def idle(url: String): Behavior[InfoCommand] = {
    Behaviors.receive[InfoCommand]{(ctx, msg) =>
      msg match {
        case StartWork =>
          crawl.fetch(url).onComplete{t=>
            val html = t.toString
            ctx.self ! ParseHtml(html)
          }
          Behaviors.same

        case FetchUrl(url) =>
          crawl.fetch(url).onComplete{t=>
            val html = t.toString
            if(html.length <10){
              Thread.sleep(Random.nextInt(6)*1000 + 10000)
              ctx.self ! FetchUrl(url)
            }else
            ctx.self ! ParseHtml(html)
          }
          Behaviors.same

        case ParseHtml(html) =>
          crawl.getInfo(html)
          Behaviors.same
      }
    }
  }
}
