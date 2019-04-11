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
import com.neo.sk.todos2018.shared.ptcl.ToDoListProtocol.{AddRecordReq, DelRecordReq, GetListRsp}
import com.neo.sk.todos2018.shared.ptcl.{ErrorRsp, SuccessRsp}

import scala.concurrent.Future
import scala.language.postfixOps
import com.neo.sk.todos2018.ptcl.Protocols.parseError
/**
  * User: sky
  * Date: 2018/6/1
  * Time: 15:41
  */
trait ToDoListService extends ServiceUtils with SessionBase {

  import io.circe._
  import io.circe.generic.auto._

  implicit val timeout: Timeout

  implicit val scheduler: Scheduler

  private val log = LoggerFactory.getLogger(getClass)

  private val addRecord=(path("addRecord") & post) {
    userAuth(user=>
      entity(as[Either[Error, AddRecordReq]]) {
        case Left(error) =>
          log.warn(s"some error: $error")
          complete(parseError)
        case Right(req) =>
          dealFutureResult(
            ToDoListDAO.addRecord(user.userName,req.record).map{ r=>
              if(r>0){
                complete(SuccessRsp())
              }else{
                complete(ErrorRsp(1000101,"add record error"))
              }
            }
          )
      }
    )
  }

  private val delRecord=(path("delRecord") & post) {
    userAuth(user=>
      entity(as[Either[Error, DelRecordReq]]) {
        case Left(error) =>
          log.warn(s"some error: $error")
          complete(parseError)
        case Right(req) =>
          dealFutureResult(
            ToDoListDAO.delRecord(user.userName,req.record,req.time).map{ r=>
              if(r>0){
                complete(SuccessRsp())
              }else{
                complete(ErrorRsp(1000101,"add record error"))
              }
            }
          )
      }
    )
  }

  private val getList=(path("getList") & get) {
    userAuth(user=>
      dealFutureResult(
        ToDoListDAO.getRecordList(user.userName).map{ list=>
          complete(GetListRsp(Some(list)))
        }
      )
    )
  }

  val listRoutes: Route =
    pathPrefix("list") {
      addRecord ~ delRecord ~ getList
    }

}
