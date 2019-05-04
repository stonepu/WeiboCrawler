package com.neo.sk.todos2018.models.dao.BlogDao

import com.neo.sk.todos2018.utils.DBUtil.db
import slick.jdbc.PostgresProfile.api._
import com.neo.sk.todos2018.models.SlickTables._

import scala.concurrent.ExecutionContext.Implicits.global
import com.neo.sk.todos2018.common.AppSettings
import org.slf4j.LoggerFactory

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object matrixDao {
  private val log = LoggerFactory.getLogger(this.getClass)


  def addElement(user: Int, item: Int, score: Int, time: Long) = {
    val element = tUserItem += rUserItem(user, item, score, time)
    db.run(element)
  }

  def getElement(user: Int, item: Int) = {
    val element = tUserItem.filter(p => p.users===user && p.item===item).map(t => (t.users, t.item, t.score, t.time)).result
    db.run(element)
  }

  def updateElement(user: Int, item: Int, score: Int, time: Long) = {
    val list = Await.result(getElement(user, item), Duration.Inf)
    if(list.length == 0){
      addElement(user, item, score, time)
    }else{
      val scoreNew = list(0)._3
      val element = tUserItem.filter(p => p.users===user && p.item===item)
        .map(t => (t.score, t.time)).update((scoreNew+score, time))
      db.run(element)
    }
  }

  def getMatrix() = {
    val list = tUserItem.filter(p => (p.users>0))
      .map(t => (t.users, t.item, t.score, t.time)).result
    db.run(list)
  }
}
