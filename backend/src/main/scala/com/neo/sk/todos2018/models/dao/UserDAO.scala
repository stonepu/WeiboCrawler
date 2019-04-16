package com.neo.sk.todos2018.models.dao

import com.neo.sk.todos2018.utils.DBUtil.db
import slick.jdbc.PostgresProfile.api._
import com.neo.sk.todos2018.models.SlickTables._
import scala.concurrent.ExecutionContext.Implicits.global._
import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global
import com.neo.sk.todos2018.common.AppSettings
/**
  * User: sky
  * Date: 2018/6/1
  * Time: 15:17
  */
object UserDAO {

  def getUserByName(name:String)={
    //val ss = AppSettings.userMap.get(name)
    Future.successful(AppSettings.userMap.get(name))
  }

  def addUser(username: String, password: String) = {
    val addUser =
      tBloguser += rBloguser(nickname = Some(username), password = Some(password))
    db.run(addUser)
  }

  def getUserByDBName(name: String)={
    val username = tBloguser.filter(_.nickname === name).map(_.password).result
    db.run(username)
  }
}
