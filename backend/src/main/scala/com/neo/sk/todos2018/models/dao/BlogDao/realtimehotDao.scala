package com.neo.sk.todos2018.models.dao.BlogDao

import com.neo.sk.todos2018.utils.DBUtil.db
import slick.jdbc.PostgresProfile.api._
import com.neo.sk.todos2018.models.SlickTables._
import scala.concurrent.ExecutionContext.Implicits.global
import com.neo.sk.todos2018.common.AppSettings
import org.slf4j.LoggerFactory

object realtimehotDao {
  private val log = LoggerFactory.getLogger(this.getClass)

  def addHot(rank: Option[Int], title: Option[String],
             hotNum: Option[Long], url: String) = {
    val hot = tRealtimehot += rRealtimehot(rank, title, hotNum, url)
    db.run(hot)
  }

  def dtl() = {
    db.run(tRealtimehot.filter(p => !(p.url === "")).delete)
  }

  def getHot() = {
    db.run(tRealtimehot.filter(p => !(p.url==="")).sortBy(_.rank.asc)
      .map(t =>(t.rank, t.title, t.hotnum, t.url)).result)
  }
}
