package com.neo.sk.todos2018.service

import akka.actor.Scheduler
import akka.util.Timeout
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

import com.neo.sk.todos2018.models.dao.{CrawlDAO, UserDAO, ByrDAO}
import com.neo.sk.todos2018.shared.ptcl.BYRProtocol.{GetListRsp, GetArticleReq}
import com.neo.sk.todos2018.shared.ptcl.{ErrorRsp, SuccessRsp}
import com.neo.sk.todos2018.ptcl.Protocols.parseError
import scala.util.{Failure, Success}

import org.slf4j.LoggerFactory
import com.neo.sk.todos2018.Boot.executor
import scala.language.postfixOps

trait ByrService extends ServiceUtils with SessionBase {
	import io.circe._
	import io.circe.generic.auto._
	
	implicit val timeout: Timeout
	implicit val scheduler: Scheduler
	
	private val log = LoggerFactory .getLogger(getClass)
	
	private val getList=(path("getList") & pathEndOrSingleSlash & post) {
		entity(as[Either[Error, GetArticleReq]]){
			case Left(error) =>
				log.warn(s"there are some errors:$error")
				complete(parseError)
			case Right(req) =>
				dealFutureResult(
					ByrDAO.getArticleList(req.board).map{list=>
						//val s = list.toList
						var lists = List[(String, String, String)]()
						for(i<-list.toList){
							lists = (i._1.getOrElse(""), i._2.getOrElse(""), i._3.getOrElse(""))::lists
						}
						complete(GetListRsp(Some(lists)))
					}
				)
		}
	}

	
	val ByrRoutes: Route =
		pathPrefix("byr") {
			getList
		}
}
