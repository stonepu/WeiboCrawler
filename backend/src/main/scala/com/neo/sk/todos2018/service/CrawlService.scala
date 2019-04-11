package com.neo.sk.todos2018.service

import akka.actor.{ActorRef, ActorSystem, Scheduler}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.{Materializer, OverflowStrategy}
import akka.event.{Logging, LoggingAdapter}
import akka.util.Timeout
import scala.collection.JavaConversions._

import scala.concurrent.ExecutionContextExecutor
import scala.collection.mutable.{Map => mMap}
import com.neo.sk.todos2018.ptcl.Protocols.parseError
import com.neo.sk.todos2018.utils.HttpUtil
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.jsoup.nodes.Element
import org.slf4j.LoggerFactory
import scala.util.{Failure, Success}
import com.neo.sk.todos2018.common.AppSettings._
import com.neo.sk.todos2018.models.dao.CrawlDAO

trait CrawlService extends HttpUtil with ServiceUtils {
	implicit val system: ActorSystem
	
	implicit val executor: ExecutionContextExecutor
	
	implicit val materializer: Materializer
	
	val log = LoggerFactory.getLogger("com.neo.sk.todos2018.service.CrawlService")
	
	
}
