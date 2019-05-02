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

object UrlSaveDao {
  private val log = LoggerFactory.getLogger(this.getClass)

  def delete() = {
    val delete = tUrlsave.filter(p => !(p.url==="")).delete
    db.run(delete)
  }

  def updateUrl(url: String) = {
    db.run(tUrlsave += rUrlsave(url))
  }

  def getUrl() = {
    db.run(tUrlsave.filter(p=> !(p.url === "")).map(i=>i.url).result)
  }
}
