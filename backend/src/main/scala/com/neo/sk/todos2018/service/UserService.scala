package com.neo.sk.todos2018.service

import akka.actor.Scheduler
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import org.slf4j.LoggerFactory
import com.neo.sk.todos2018.Boot.executor
import com.neo.sk.todos2018.models.dao.BlogDao.{BlogDao, BlogUserDao}
import com.neo.sk.todos2018.models.dao.UserDAO
import com.neo.sk.todos2018.ptcl.UserProtocol.UserBaseInfo
import com.neo.sk.todos2018.service.SessionBase.{SessionKeys, SessionTypeKey, ToDoListSession}
import com.neo.sk.todos2018.shared.ptcl.{ErrorRsp, SuccessRsp}
import com.neo.sk.todos2018.shared.ptcl.UserProtocol.{UserLoginReq, UserSignupReq}

import scala.concurrent.Future
import scala.language.postfixOps
import com.neo.sk.todos2018.ptcl.Protocols.parseError

/**
  * User: sky
  * Date: 2018/4/24
  * Time: 15:31
  */
trait UserService extends ServiceUtils with SessionBase {

  import io.circe._
  import io.circe.generic.auto._

  implicit val timeout: Timeout

  implicit val scheduler: Scheduler

  private val log = LoggerFactory.getLogger(getClass)

  val secretKey = "dsacsodaux84fsdcs4wc32xm"

  //用户登录
  private val userLogin=(path("userLogin") & pathEndOrSingleSlash & post) {
    entity(as[Either[Error, UserLoginReq]]) {
      case Left(error) =>
        log.warn(s"some error: $error")
        complete(parseError)
      case Right(req) =>
        dealFutureResult(
          UserDAO.getUserByDBName(req.userName).map{pwd=>
            if(pwd.isEmpty){
              complete(ErrorRsp(100102,"userName not exist"))
            }else{
              val ss = pwd.toList.head
              if (pwd.toList.head.getOrElse("") == req.pwd) {
                val session = ToDoListSession(UserBaseInfo(req.userName), System.currentTimeMillis()).toSessionMap
                addSession(session) {
                  log.info(s"user ${req.userName} login success")
                  complete(SuccessRsp())
                }
              }else {
                complete(ErrorRsp(100103, "pwd is wrong"))
              }
            }
          }
        )
    }
  }

  private val userLogout=(path("userLogout") & get) {
    userAuth{user=>
      val session=Set(SessionTypeKey,SessionKeys.name)
      removeSession(session){ctx =>
        log.info(s"user-----${user.userName}----logout")
        ctx.complete(SuccessRsp())
      }
    }
  }

  //用户注册

  private val userSubmit=(path("userSubmit") & pathEndOrSingleSlash & post) {
    entity(as[Either[Error, UserSignupReq]]) {
      case Left(error) =>
        log.warn(s"some error: $error")
        complete(parseError)
      case Right(req) =>
        dealFutureResult(
          UserDAO.getUserByDBName(req.username).map{ user =>
            if(!user.isEmpty){
              complete(ErrorRsp(100113, "user already exists!"))
            }else{
              if(req.pwdAgain == req.pwd){
                val session = ToDoListSession(UserBaseInfo(req.username), System.currentTimeMillis()).toSessionMap
                addSession(session){
                  log.info(s"${req.username} sign up success")
                  BlogUserDao.addUser(Some(req.username), Some(""), Some(""),Some(""), Some(""),
                    Some(""), Some(""), Some(""), Some(""), Some(""), Some(""), Some(req.pwd))
                  complete(SuccessRsp())
                }
              }else{
                complete(ErrorRsp(100114, "the two passwords does not equal"))
              }
            }
          }
        )
    }
  }

  val userRoutes: Route =
    pathPrefix("user") {
      userLogin ~ userLogout ~ userSubmit
    }

}