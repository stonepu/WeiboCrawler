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
import scala.util.{Failure, Success}
import com.neo.sk.todos2018.Boot.spiderActor

object Comment {

  private val log = LoggerFactory.getLogger(this.getClass)
  sealed trait CommentCommand

  private case object TimeOutMsg extends CommentCommand


  case object StartWork extends CommentCommand
  case class ParseHtml( html: String) extends CommentCommand
  case class FetchUrl(url: String) extends  CommentCommand

  val behavior: Behavior[CommentCommand] = {
    Behaviors.setup[CommentCommand]{ctx =>
      Behaviors.withTimers{implicit timer =>
        timer.startSingleTimer(TimeOutMsg, StartWork, 5.seconds)
        idle()
      }
    }
  }

  def idle(): Behavior[CommentCommand] = {
    Behaviors.receive[CommentCommand]{(ctx, msg) =>
      msg match {
        case StartWork =>
          Behaviors.same

        case FetchUrl(url) =>
          crawl.fetch(url).onComplete{t=>
            if(t.toString.length > 10){
              ctx.self ! ParseHtml(t.toString)
            }
          }
          Behaviors.same

        case ParseHtml(html) =>
          //fixme 解析评论
          Behaviors.same
      }
    }
  }
}
