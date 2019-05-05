package com.neo.sk.todos2018.models.dao.BlogDao

import com.neo.sk.todos2018.utils.DBUtil.db
import slick.jdbc.PostgresProfile.api._
import com.neo.sk.todos2018.models.SlickTables._
import com.neo.sk.todos2018.Boot.executor
import org.slf4j.LoggerFactory
import scala.util.Random

object BlogDao {
  private val log = LoggerFactory.getLogger(this.getClass)

  def addBlog(author: Option[String], homeUrl: Option[String],
              content: String, time: Option[Long],
              like: Option[String], forward: Option[String],
              comment: Option[String], commentUrl: String) = {
    val addBlog =
      tBlog += rBlog(author, homeUrl, content, time, like, forward,
        comment, commentUrl, item2int = -1)
    db.run(addBlog)
  }

  def findBlog(content: Option[String]) = {
    val blog = tBlog.filter(_.content === content).map(_.content).result
    db.run(blog)
  }

  def updateBlog(author: Option[String], homeUrl: Option[String],
             content: String, time: Option[Long],
             like: Option[String], forward: Option[String],
             comment: Option[String], commentUrl: String) = {
    findBlog(Some(content)).map{tempContent =>
      if(tempContent.isEmpty){
        addBlog(author: Option[String], homeUrl: Option[String],
          content: String, time: Option[Long],
          like: Option[String], forward: Option[String],
          comment: Option[String], commentUrl: String)
      }
      else{
        val update = tBlog.filter(_.content === content).map(p=>
          (p.author, p.homeurl, p.content, p.time, p.like,
            p.forward, p.comment, p.commenturl)
        ).update(author, homeUrl, content, time,
          like, forward, comment, commentUrl)
        db.run(update)
      }
    }
  }

  def getContent(nickname: String="Non", home: String="Non") = {
    val content = tBlog.filter(p => p.author === nickname || p.homeurl===home).sortBy(_.time.desc)
      .map(a => (a.content, a.commenturl, a.like, a.forward,
        a.comment, a.time, a.author, a.item2int)).result
    db.run(content)
  }

  def publish(nickname: String, content: String) = {
    var homeUrl = ""
    BlogUserDao.nickname2Url(nickname).onComplete{t=>
      homeUrl = t.get.toList.head.get
      val blog = tBlog += rBlog(Some(nickname), Some(homeUrl), content,
        Some(System.currentTimeMillis()), Some("0"), Some("0"), Some("0"),Random.nextString(10), item2int = -1)
      //log.info("正在存入数据库")
      db.run(blog)
    }
  }

  def like(commentUrl: String, likes: String) = {
    val likeNum = likes.toInt + 1
    val like = tBlog.filter(_.commenturl === commentUrl).map(p => p.like).update(Some(likeNum.toString))
    db.run(like)
  }

  def getCTime(commentUrl: String) = {
    val time = tBlog.filter(_.commenturl === commentUrl).map(p => p.ctime).result
    db.run(time)
  }

  def updateCTime(commentUrl: String, time: Long) = {
    val times = tBlog.filter(_.commenturl === commentUrl).map(p => p.ctime).update(Some(time))
    db.run(times)
  }

  def blog2num(commentUrl: String) = {
    val num = tBlog.filter(_.commenturl === commentUrl).map(p => p.item2int).result
    db.run(num)
  }

}
