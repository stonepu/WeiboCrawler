package com.neo.sk.todos2018.coreblog

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.{Behaviors, StashBuffer, TimerScheduler}

import scala.concurrent.duration._

object Parse {
  trait ParseCommand

  private object TimerKey
  case object TimeOutMsg extends ParseCommand
  case object StartWork extends ParseCommand
  case object FinishTask extends ParseCommand
  case class Parse(html: String) extends ParseCommand
  case class Store(string: String) extends ParseCommand

  def init(url: String):Behavior[ParseCommand] = {
    Behaviors.setup[ParseCommand] { ctx =>
      implicit val stashBuffer = StashBuffer[ParseCommand](Int.MaxValue)
      Behaviors.withTimers[ParseCommand] { implicit timer =>
        timer.startSingleTimer(TimeOutMsg, StartWork, 5.seconds)
        idle()
      }
    }
  }

  def idle()(implicit timer: TimerScheduler[ParseCommand]):Behavior[ParseCommand] = {
    Behaviors.receive[ParseCommand]{(ctx, msg) =>
      msg match{
        case Parse(html) =>
          //解析
          val string = ""
          ctx.self ! Store(string)
          Behavior.same
        case Store(string) =>
          //数据库存储
          ctx.self ! FinishTask
          Behavior.same

      }
    }
  }

}
