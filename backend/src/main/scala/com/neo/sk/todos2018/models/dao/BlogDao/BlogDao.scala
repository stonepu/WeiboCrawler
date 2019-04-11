package com.neo.sk.todos2018.models.dao.BlogDao

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

object BlogDao {
  private val log = LoggerFactory.getLogger(this.getClass)

  def addBlog(author: Option[String], homeUrl: Option[String],
              content: Option[String], time: Option[String],
              like: Option[String], forward: Option[String],
              comment: Option[String], commentUrl: Option[String]) = {
    val addBlog =
      tBlog += rBlog(author, homeUrl, content, time, like, forward,
        comment, commentUrl)
    db.run(addBlog)
  }

  def findBlog(content: Option[String]) = {
    val blog = tBlog.filter(_.content === content).map(_.homeurl).result
    db.run(blog)
  }

  def updateBlog(author: Option[String], homeUrl: Option[String],
             content: Option[String], time: Option[String],
             like: Option[String], forward: Option[String],
             comment: Option[String], commentUrl: Option[String]) = {
    findBlog(content).map{tempHome =>
      if(tempHome.isEmpty){
        addBlog(author: Option[String], homeUrl: Option[String],
          content: Option[String], time: Option[String],
          like: Option[String], forward: Option[String],
          comment: Option[String], commentUrl: Option[String])
      }
      else{
        val update = tBlog.filter(_.homeurl === homeUrl).map(p=>
          (p.author, p.homeurl, p.content, p.time, p.like,
            p.forward, p.comment, p.commenturl)
        ).update(author, homeUrl, content, time,
          like, forward, comment, commentUrl)
        db.run(update)
      }
    }
  }

}
