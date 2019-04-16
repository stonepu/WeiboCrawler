package com.neo.sk.todos2018.service

import akka.actor.Scheduler
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import org.slf4j.LoggerFactory
import com.neo.sk.todos2018.Boot.executor
import com.neo.sk.todos2018.models.dao.{ToDoListDAO, UserDAO}
import com.neo.sk.todos2018.ptcl.UserProtocol.UserBaseInfo
import com.neo.sk.todos2018.service.SessionBase.ToDoListSession
import com.neo.sk.todos2018.shared.ptcl.BlogPtcl.{GetContentByPageReq, GetContentByPageRsp, GetContentReq, GetContentRsp}
import com.neo.sk.todos2018.models.dao.BlogDao.BlogDao
import com.neo.sk.todos2018.shared.ptcl.{ErrorRsp, SuccessRsp}

import scala.concurrent.Future
import scala.language.postfixOps
import com.neo.sk.todos2018.ptcl.Protocols.parseError

import scala.collection.mutable.ListBuffer

trait BlogService extends ServiceUtils with SessionBase{

  import io.circe._
  import io.circe.generic.auto._

  implicit val timeout: Timeout

  implicit val scheduler: Scheduler

  private val log = LoggerFactory.getLogger(getClass)

  val getContent = (path("getContent") & post & pathEndOrSingleSlash) {
    userAuth (
      _ =>
        entity(as[Either[Error, GetContentReq]]){
          case Left(error) =>
            log.warn(s"some error: $error")
            complete(parseError)
          case Right(req) =>
            dealFutureResult(
              BlogDao.getContent(req.nickname).map{list=>
                val lists = ListBuffer[String]()
                for(i<-list.toList){
                  lists += i.getOrElse("None")
                }
                complete(GetContentRsp(lists.toList))
              }
            )
        }
    )
  }

  val getContentByPage = (path("getContentByPage") & post & pathEndOrSingleSlash) {
    userAuth{
      _ =>
        entity(as[Either[Error, GetContentByPageReq]]){
          case Left(error) =>
            log.warn(s"some error: $error")
            complete(parseError)
          case Right(req) =>
            dealFutureResult(
              BlogDao.getContent(req.nickname).map{ p=>
                complete(GetContentByPageRsp(p.toList.map(i => i.getOrElse("")).slice((req.page-1)*20,req.page*20), p.length))
              }
            )
        }
    }
  }


  val BlogRoutes: Route =
    pathPrefix("blog") {
      getContent ~ getContentByPage
    }
}
