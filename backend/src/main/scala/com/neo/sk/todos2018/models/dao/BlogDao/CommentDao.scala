package com.neo.sk.todos2018.models.dao.BlogDao

import com.neo.sk.todos2018.utils.DBUtil.db
import slick.jdbc.PostgresProfile.api._
import com.neo.sk.todos2018.models.SlickTables._
import com.neo.sk.todos2018.common.AppSettings
import org.slf4j.LoggerFactory

object CommentDao {
  private val log = LoggerFactory.getLogger(this.getClass)

  def addComment(reviewer: String, reviewed: String, content: String,
                 commentUrl: String, time: Long=0L) = {
    //println("+++++++++adding comment++++++++++++")
    db.run(tComment += rComment(reviewer, Some(reviewed), Some(content), Some(commentUrl), Some(time)))
  }

  def getCommentWithParas(reviewer: String="", reviewed: String="", content: String="",
                          commentUrl: String, time: Long=0) = {
    val comment = tComment.filter(p => p.commenturl === commentUrl && p.reviewer ===reviewer
      && p.reviewed === reviewed && p.content === content && p.time === time)
      .map(t=>(t.reviewer, t.reviewed, t.content, t.commenturl, t.time)).result
    db.run(comment)
  }

  def getFirstComment(commentUrl: String) = {
    val comment = tComment.filter(_.commenturl === commentUrl).sortBy(_.time.desc).map(p =>
      (p.reviewer, p.reviewed, p.content, p.time)).result
    db.run(comment)
  }

}
