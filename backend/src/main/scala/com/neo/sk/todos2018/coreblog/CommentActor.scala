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
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Random, Success}
import com.neo.sk.todos2018.Boot.articleActor
import com.neo.sk.todos2018.models.dao.BlogDao.{BlogDao, CommentDao}

object CommentActor {

  private val log = LoggerFactory.getLogger(this.getClass)
  sealed trait CommentCommand

  private case object TimeOutMsg extends CommentCommand


  case object StartWork extends CommentCommand
  case object FinishWork extends CommentCommand
  case class WorkOtherPage(urlQueue: mutable.Queue[(String, Int)], isIncrement: Boolean=false, timeDao: Long=0) extends CommentCommand
  case class ParseHtml( html: String) extends CommentCommand

  case class GetRemainingPageUrl(url: String, page: Int, isIncrement: Boolean=false, timeDao: Long=0) extends CommentCommand

  case object FetchUrl extends CommentCommand
  case class GetUrl(urlList: List[String]) extends CommentCommand

  val behavior: Behavior[CommentCommand] = {
    Behaviors.setup[CommentCommand]{ctx =>
      Behaviors.withTimers{implicit timer =>
        timer.startSingleTimer(TimeOutMsg, StartWork, 5.seconds)
        val hash: mutable.Queue[(String, Int)] = mutable.Queue()
        val hashSet: mutable.HashSet[String] = mutable.HashSet()
        //urlList.foreach(t => hash.enqueue((t, 0)))
        idle(hash, hashSet)
      }
    }
  }

  def idle(hash: mutable.Queue[(String, Int)],
           invalidSet: mutable.HashSet[String],
           )(implicit timer: TimerScheduler[CommentCommand]): Behavior[CommentCommand] = {
    Behaviors.receive[CommentCommand]{(ctx, msg) =>
      msg match {
        case StartWork =>
          ctx.self ! FetchUrl
          Behaviors.same

        case FetchUrl =>
          if(!hash.isEmpty){
            val url = hash.dequeue()
            crawl.fetch(url._1).onComplete{t=>
              val html = t.get
              if(html.length > 10){
                //fixme 分页
                val page = crawl.getCommentPage(html)
                val firstComment = Await.result(CommentDao.getFirstComment(commentUrl = url._1), Duration.Inf)
                if(firstComment.length == 0){
                  crawl.parseComment(html, url._1)
                  ctx.self ! GetRemainingPageUrl(url._1, page, false)
                }else{
                  val timeDao = Await.result(BlogDao.getCTime(url._1), Duration.Inf)(0).get
                  crawl.parseIncrementalComment(html, url._1,timeDao)
                  ctx.self ! GetRemainingPageUrl(url._1, page, true, timeDao)
                }
              } else{
                if(url._2 > 2) invalidSet += url._1
                else hash.enqueue((url._1, url._2+1))
              }
              ctx.self ! FetchUrl
            }
          } else ctx.self ! FinishWork
          Behaviors.same

        case GetRemainingPageUrl(url, page, isIncrement, timeDao) =>
          if(page > 1){
            val pages = if(page > 100) 100 else page
            val urlQueue: mutable.Queue[(String, Int)] = mutable.Queue()
            for(i<- 2 to pages){
              val urlPage = url + s"?page=${i}"
              urlQueue.enqueue((urlPage, 0))
            }
            ctx.self ! WorkOtherPage(urlQueue, isIncrement, timeDao)
          }
          Behaviors.same

        case WorkOtherPage(urlQueue, isIncrement, timeDao) =>
          if(urlQueue.length > 0){
            val urlTemp = hash.dequeue()
            crawl.fetch(urlTemp._1).onComplete{t=>
              val html = t.get
              if(html.length < 10){
                log.error(s"get url $urlTemp error!,重新放入队列")
                if(urlTemp._2 > 2) invalidSet += urlTemp._1
                else hash.enqueue((urlTemp._1, urlTemp._2+1))
              }else{
                if(isIncrement) crawl.parseIncrementalComment(html, urlTemp._1, timeDao)
                else crawl.parseComment(html, urlTemp._1)
              }
            }
            if(urlQueue.length > 0){
              Thread.sleep(Random.nextInt(6)*1000 + 5000)
              ctx.self ! WorkOtherPage(urlQueue, isIncrement)
            }else ctx.self ! FinishWork
          }
          Behaviors.same

        case GetUrl(urlList) =>
          urlList.foreach(t => hash.enqueue((t, 0)))
          if(hash.length == urlList.length) ctx.self ! FetchUrl
          Behaviors.same


        case FinishWork =>
          if(invalidSet.toList.length>1000000) invalidSet.clear()
          Behaviors.same
      }
    }
  }
}
