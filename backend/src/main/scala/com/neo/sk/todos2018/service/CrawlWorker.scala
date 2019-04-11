package com.neo.sk.todos2018.service

import akka.actor.{ActorRef, ActorSystem, Scheduler}
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
import scala.concurrent.ExecutionContext.Implicits.global
import com.neo.sk.todos2018.common.AppSettings._
import com.neo.sk.todos2018.models.dao.CrawlDAO



object CrawlWorker extends HttpUtil {
	
	//implicit val system: ActorSystem
	
	//implicit val executor: ExecutionContextExecutor
	
	//implicit val materializer: Materializer
	
	//val log = LoggerFactory.getLogger(getClass)
	
	val log = LoggerFactory.getLogger(getClass)
	
	//val url2 = "https://bbs.byr.cn/section/0?_uid=xiaopuwrl"
	
	var boardAndLink = Map[String,String]()
	var articleLists = List[(String, String)]()//文章目录
	
	def parseBoard(html: String)={      //通过使用for循环遍历所有section
		val doc = Jsoup.parse(html)
		val link = doc.select("a[href]")
		for(links<- link){
			if(links.attr("href").startsWith("/board")) {
				boardAndLink += (links.text() -> links.attr("href"))
			}
		}
	}
	
	//解析出文章目录，返回文章目录列表
	def parseArticleCatalog(html: String)={
		var article = List[(String, String)]()
		val doc = Jsoup.parse(html)
		val articles = doc.select("td[class=title_9]")
		for(elem<- articles){
			articleLists = (s"${elem.text()}" ,s"${elem.select("a[href]").attr("href")}")::articleLists
			article = (s"${elem.text()}" ,s"${elem.select("a[href]").attr("href")}")::article

			println((s"${elem.text()}" ,s"${elem.select("a[href]").attr("href")}"))
		}
		//articleLists = (("*"*20, "_"*20))::articleLists
		article
	}
	
	//解析文章内容
	def parseArticle(html: String, section: String)={
		val doc = Jsoup.parse(html)
		val article = doc.select(".a-content-wrap").first()
		val articleContent = article.text
		val authArray = articleContent.split("信区: ")
		val author = authArray(0).drop(5).dropRight(2)
		val boardTemp = authArray(1).split(" 标 题: ")
		val titleTemp = boardTemp(1).split(" 发信站: ")
		val contentArr = titleTemp(1).split("站内 ")(1).split(" -- ")
		var content = contentArr(0)
		content =  if(!content.startsWith("--")) content else ""
		//CrawlDAO.update(section, boardTemp(0), titleTemp(0), content, author, "comment")
		println((s"$section", boardTemp(0), titleTemp(0), content, author))
	}
	
	def getBoard(html: String)= {
		//parseBoard(html, boardList)
		for(i<-0 to 0) {
			val url = s"${url0}section/${i+crawlUser}"
			getRequestSend("get",url,para,headers,"GBK").map {
				case Left(error) =>
					log.error(s"error: $error")
				case Right(sBoard) =>
					parseBoard(sBoard)
			}
		}
	}
	
	def getArticleCatalog(boardList: List[(String, String)]) ={
		for(board<- boardList) {
			val urlBoard = s"${url0 + board._2.drop(1) + crawlUser}"
			println(urlBoard)
			getRequestSend("get", urlBoard, para, headers, "GBK").onComplete{
				case Success(t) => t.map{
					case s =>
						if(s.length<100) println(s)
						else{
							parseArticleCatalog(s)
						}
					//getArticle(articleLists)
				}
				case Failure(error) =>
					log.error(s"error: $error")
			}
			Thread.sleep(7000)
		}
	}
	
	def getArticle(articleList: List[(String, String)])= {
		for(articleAdd<- articleList) {
			val urlArticle = s"${url0 + articleAdd._2 + crawlUser}"
			getRequestSend("get", urlArticle, para, headers, "GBK").onComplete{
				case Success(t) => t.map{
					case sA =>
						if(sA.length<100) println(sA)
						else{
							parseArticle(sA,"")
						}
				}
				case Failure(error) =>
					log.error(s"some error: $error")
			}
			Thread.sleep(6500)
		}
	}

	def getArticle(title: String, articleAddr: String)= {
			val urlArticle = s"${url0 + articleAddr + crawlUser}"
			getRequestSend("get", urlArticle, para, headers, "GBK").onComplete{
				case Success(t) => t.map{
					case sA =>
						if(sA.length<100) println(sA)
						else{
							parseArticle(sA,"")
						}
				}
				case Failure(error) =>
					log.error(s"some error: $error")
			}
			//Thread.sleep(6500)
	}

	def getArticleCatalog(boardName: String, boardAddr: String) ={
		val urlBoard = s"${url0 + boardAddr.drop(1) + crawlUser}"
		var articleList = List[(String, String)]()
		getRequestSend("get", urlBoard, para, headers, "GBK").onComplete{
			case Success(t) => t.map{
				case s =>
					if(s.length<100) println(s)
					else{
						articleList = parseArticleCatalog(s)
					}
			}
			case Failure(error) =>
				log.error(s"error: $error")
		}
		articleList
	}

}
