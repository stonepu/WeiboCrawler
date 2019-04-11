package com.neo.sk.todos2018.models.dao

import com.neo.sk.todos2018.utils.DBUtil.db
import slick.jdbc.PostgresProfile.api._
import com.neo.sk.todos2018.models.SlickTables._
import com.neo.sk.todos2018.common.AppSettings._

import scala.concurrent.ExecutionContext.Implicits.global._
import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global
import com.neo.sk.todos2018.common.AppSettings
import org.slf4j.LoggerFactory
import com.neo.sk.todos2018.service.ServiceUtils

object ByrDAO extends ServiceUtils{
	private val log = LoggerFactory.getLogger(this.getClass)
	
	def getArticleList(board: String)={
		val article = tByrbbs.filter(p=>(p.board === board)).
			map(a => (a.title,a.content, a.author)).result
		db.run(article)
	}
}
