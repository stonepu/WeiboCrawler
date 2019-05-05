package com.neo.sk.todos2018.service

import akka.actor.Scheduler
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import org.slf4j.LoggerFactory
import com.neo.sk.todos2018.Boot.executor
import com.neo.sk.todos2018.service.SessionBase.{SessionKeys, SessionTypeKey}
import com.neo.sk.todos2018.shared.ptcl.BlogPtcl._
import com.neo.sk.todos2018.models.dao.BlogDao._
import com.neo.sk.todos2018.shared.ptcl.{ErrorRsp, SuccessRsp}
import io.circe.syntax._
import io.circe.parser._
import io.circe.generic.auto._

import scala.concurrent.{Await, Future}
import com.neo.sk.todos2018.ptcl.Protocols.parseError

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future
import scala.concurrent.duration.Duration

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
                val lists = ListBuffer[BlogInfo]()
                for(i<-list.toList){
                  lists += BlogInfo(i._1, i._2, i._3.getOrElse(""), i._4.getOrElse(""), i._5.getOrElse(""), i._6.getOrElse(0L))
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
                complete(GetContentByPageRsp(p.toList.distinct.map(i => BlogInfo(i._1, i._2, i._3.getOrElse(""), i._4.getOrElse(""), i._5.getOrElse(""), i._6.getOrElse(0L), comment2Int = i._8)).slice((req.page-1)*10,req.page*10), p.length))
              }
            )
        }
    }
  }

  val logout = (path("logout") & get){
    userAuth{user=>
      val session=Set(SessionTypeKey,SessionKeys.name)
      removeSession(session){ctx =>
        log.info(s"user-----${user.userName}----logout")
        ctx.complete(SuccessRsp())
      }
    }
  }

  val publishing = (path("publish") & post & pathEndOrSingleSlash){
    userAuth{
      _ =>
        entity(as[Either[Error, PublishReq]]){
          case Left(error)=>
            log.warn(s"some error: $error")
            complete(parseError)
          case Right(req) =>
            dealFutureResult(
              Future{
                log.info("进入发布函数")
                BlogDao.publish(req.username, req.content)
                complete(SuccessRsp(0,"ok"))
              }
            )

        }
    }
  }

  val like = (path("like") & post & pathEndOrSingleSlash){
    userAuth(
      _ =>
        entity(as[Either[Error, LikeReq]]){
          case Left(error) =>
            log.warn(s"some error: $error")
            complete(parseError)
          case Right(req) =>
            dealFutureResult(
              BlogDao.like(req.commentUrl, req.like).map{ r=>
                if(r>=0){
                  complete(SuccessRsp())
                }else{
                  log.warn(s"add like error")
                  complete(ErrorRsp(100121,"add like error"))
                }
              }
            )
        }
    )
  }

  val getInfo = (path("getInfo") & post & pathEndOrSingleSlash){
    userAuth(
      _=>
        entity(as[Either[Error, GetUserInfoReq]]){
          case Left(error)=>
            log.warn(s"some error: $error")
            complete(parseError)
          case Right(req)=>
            dealFutureResult(
              BlogUserDao.getInfo(req.nickname).map{p=>
                val list = ListBuffer[UserInfo]()
                for(i<- p.toList){
                  val follow = i._10.getOrElse("").split("|").toList
                  val fans = i._11.getOrElse("").split("|").toList
                  list += UserInfo(i._1.getOrElse(""), i._2.getOrElse(""), i._3.getOrElse(""),
                    i._4.getOrElse(""), i._5.getOrElse(""), i._6.getOrElse(""),
                    i._7.getOrElse(""), i._8.getOrElse(""), i._9.getOrElse(""),
                    follow, fans)
                }
                complete(GetUserInfoRsp(list.toList, p.length))
              }
            )
        }
    )
  }

  val getFansInfo = (path("getFansInfo") & post & pathEndOrSingleSlash){
    userAuth(
      _ =>
        entity(as[Either[Error, GetUserInfoByPageReq]]){
          case Left(error)=>
            log.warn(s"some error: $error")
            complete(parseError)
          case Right(req) =>
            dealFutureResult(
              Future{
                val v1 = Await.result(BlogUserDao.getFans(nickname=req.nickname), Duration.Inf)
                val infoList = ListBuffer[UserInfo]()
                println(s"fans 's length :")
                println(v1.toList(0).get.split("\\|").length)
                for(link<- v1.toList(0).get.split("\\|").toList){
                  println(s"==========请求数据")
                  println(link)
                  val infos = Await.result(BlogUserDao.getInfo(home = link), Duration.Inf).toList
                  println(s"========info")
                  if(infos.length>0){
                    val info = infos(0)
                    infoList += UserInfo(info._1.getOrElse("None"),info._2.getOrElse("None"),
                      info._3.getOrElse("None"),info._4.getOrElse("None"),
                      info._5.getOrElse("None"),info._6.getOrElse("None"),
                      info._7.getOrElse("None"),info._8.getOrElse("None"),
                      info._9.getOrElse("None"))
                  }
                }
                complete(GetUserInfoRsp(infoList.toList, infoList.length))
              }

            /*BlogUserDao.getFans(nickname = req.nickname).map{t =>
                  val infoList = ListBuffer[UserInfo]()
                  println(s"${req.nickname}")
                  println(t.toList.length)
                  val list = t.toList(0).get.split("\\|").toList
                  println(list)
                  for(link<- list){
                    BlogUserDao.getInfo(home = link).map{infoSeq =>
                      println(link)
                      println(s"===========正在请求数据")
                      println(s"=======infoSeq")
                      println(infoSeq.toList.length)
                      for(i<-infoSeq.toList)
                        println(i+"\r\n")
                      val info = infoSeq.toList(0)
                      infoList += UserInfo(info._1.getOrElse("None"),info._2.getOrElse("None"),
                        info._3.getOrElse("None"),info._4.getOrElse("None"),
                        info._5.getOrElse("None"),info._6.getOrElse("None"),
                        info._7.getOrElse("None"),info._8.getOrElse("None"),
                        info._9.getOrElse("None"))
                    }
                  }
                  println(s"infoList的length为：${infoList.toList.length}")
                  complete(GetUserInfoRsp(infoList.toList, infoList.length))
                }*/
            )
        }
    )
  }

  val getFollowInfo = (path("getFollowInfo") & post & pathEndOrSingleSlash){
    userAuth(
      _ =>
        entity(as[Either[Error, GetUserInfoByPageReq]]){
          case Left(error)=>
            log.error(s"some error: $error")
            complete(parseError)
          case Right(req) =>
            dealFutureResult(
              Future{
                val v1 = Await.result(BlogUserDao.getFollow(nickname=req.nickname), Duration.Inf)
                val infoList = ListBuffer[UserInfo]()
                for(link<- v1.toList(0).get.split("\\|")){
                  val infos = Await.result(BlogUserDao.getInfo(home = link), Duration.Inf).toList
                  if(infos.length>0){
                    val info = infos(0)
                    infoList += UserInfo(info._1.getOrElse("None"),info._2.getOrElse("None"),
                      info._3.getOrElse("None"),info._4.getOrElse("None"),
                      info._5.getOrElse("None"),info._6.getOrElse("None"),
                      info._7.getOrElse("None"),info._8.getOrElse("None"),
                      info._9.getOrElse("None"))
                  }
                }
                complete(GetUserInfoRsp(infoList.toList, infoList.length))
              }
            )
        }
    )
  }

  val getFollowByPage = (path("getFollowByPage") & post & pathEndOrSingleSlash){
    userAuth(
      _ =>
        entity(as[Either[Error, GetContentByPageReq]]){
          case Left(error) =>
            log.error(s"some error: $error")
            complete(parseError)
          case Right(req) =>
            dealFutureResult(
              Future{
                val v1 = Await.result(BlogUserDao.getFollow(nickname=req.nickname), Duration.Inf)
                val blogList = ListBuffer[BlogInfo]()
                for(link<- v1(0).get.split("\\|")){
                  val blogs = Await.result(BlogDao.getContent(home = link), Duration.Inf).toList
                  if(blogs.length>0)
                    blogs.foreach(blog => blogList += BlogInfo(blog._1, blog._2, blog._3.get,blog._4.get,blog._5.get, blog._6.get, blog._7.getOrElse("")))
                }
                complete(GetContentByPageRsp(blogList.toList.sortWith(_.time>_.time).slice((req.page-1)*10,req.page*10), blogList.length))
              }
            )
        }
    )
  }

  val sendMatrix = (path("sendMatrix") & get){
    parameter('id.as[String]){ id =>
      dealFutureResult(
        matrixDao.getMatrix().map{ t =>
          val list = ListBuffer[MatrixElement]()
          t.foreach(ele => list+=MatrixElement(ele._1, ele._2, ele._3, ele._4))
          complete(list.toList.asJson.noSpaces)
        }
      )
    }
  }

  val getMatrix = (path("getMatrix") & post & pathEndOrSingleSlash){
    entity(as[Either[Error, Json]]){
      case Left(error) =>
        log.error(s"some error: $error")
        complete(parseError)
      case Right(value) =>
        dealFutureResult(
          Future{
//            println("=====get data form python=========")
            //fixme 数据处理
            println(value)
            complete(SuccessRsp)
          }
        )
    }
  }

  val getHot = (path("getHot") & get ){
    userAuth(
      _ =>
        dealFutureResult(
          realtimehotDao.getHot().map{t =>
            val list = ListBuffer[HotInfo]()
            t.foreach(info => list += HotInfo(info._1.get, info._2.get, info._3.get, info._4))
            complete(GetHotRsp(list.toList))
          }
        )
    )
  }

  val getComment = (path("getComment") & post & pathEndOrSingleSlash){
    userAuth(
      _ =>
        entity(as[Either[Error, GetCommentReq]]){
          case Left(error)=>
            log.error(s"some error: $error")
            complete(parseError)
          case Right(req)=>
            dealFutureResult(
              CommentDao.getFirstComment(req.commentUrl).map{t =>
                val list = ListBuffer[CommentInfo]()
                t.foreach(info => list+=CommentInfo(info._1, info._2.getOrElse(""), info._3.getOrElse(""), info._4.getOrElse(0L)))
                complete(GetCommentRsp(list.toList))
              }
            )
        }
    )
  }

  val BlogRoutes: Route =
    pathPrefix("blog") {
      getContent ~ getContentByPage ~ logout ~ publishing ~ like ~
        getInfo ~ getFansInfo ~ getFollowInfo ~ getFollowByPage ~
        sendMatrix ~ getMatrix ~ getHot ~ getComment
    }
}
