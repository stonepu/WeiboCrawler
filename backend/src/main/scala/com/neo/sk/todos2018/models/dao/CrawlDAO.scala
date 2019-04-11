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

object CrawlDAO extends ServiceUtils {
	private val log = LoggerFactory.getLogger(this.getClass)
	
	def addArticle(section: String, board: String, title: String, content: String, author:String, comment: String) = {
		val addArticle =
			tByrbbs += rByrbbs(Some(section), Some(board), Some(title), Some(content), Some(author), Some(comment))
		db.run(addArticle)
	}
	
	def findArticle(section: String, board: String, title: String, content: String, author:String, comment: String)={
		val article = tByrbbs.filter(_.content === content).map(_.title).result
		db.run(article)
	}
	
	def deleteArticle(section: String, board: String, title: String, content: String, author:String, comment: String)={
		val delArticle = tByrbbs.filter(_.content === content).delete
		db.run(delArticle)
	}

	def update(section: String, board: String, title: String, content: String, author:String, comment: String)={
		findArticle(section,board,title,content, author,comment).map{titleTemp =>
			if(titleTemp.isEmpty) addArticle(section, board, title, content, author, comment)
			else{
				//deleteArticle(section: String, board: String, title: String, content: String, author:String, comment: String)
				val updateArticle = tByrbbs.filter(_.content === content).
					map(p => (p.section, p.board, p.title, p.content, p.author, p.comment)).
					update((Some(section), Some(board), Some(title), Some(content), Some(author), Some(comment)))
				db.run(updateArticle)
			}
		}
	}
	
	/*def getList(section: String)={
		val list =
	}*/
	
}
