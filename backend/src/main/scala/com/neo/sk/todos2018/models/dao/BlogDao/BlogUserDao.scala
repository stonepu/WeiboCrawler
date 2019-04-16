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

object BlogUserDao {
  private val log = LoggerFactory.getLogger(this.getClass)

  def addUser(nickname: Option[String], homeUrl: Option[String],
              imageUrl: Option[String], gender: Option[Char],
              certification: Option[String], introduction: Option[String],
              region: Option[String], birth: Option[String],
              photo: Option[String], follow: Option[String],
              fans: Option[String], password: Option[String] = Some("123")) = {
    val addUser =
      tBloguser += rBloguser(nickname, homeUrl, imageUrl, Some(gender.get.toString), certification, introduction,
           region, birth, photo, follow, fans, password)
    db.run(addUser)
  }

  def findUser(homeUrl: String) = {
    val user = tBloguser.filter(_.homeurl === homeUrl).map(_.imageurl).result
    db.run(user)
  }

  def updateUser(nickname: Option[String], homeUrl: Option[String],
                 imageUrl: Option[String], gender: Option[Char],
                 certification: Option[String], introduction: Option[String],
                 region: Option[String], birth: Option[String],
                 photo: Option[String], follow: Option[String],
                 fans: Option[String], password: Option[String] = Some("123")) = {
    findUser(homeUrl.get).map{tempImag =>
      if(tempImag.isEmpty) {
        addUser(nickname, homeUrl, imageUrl, gender, certification, introduction,
          region, birth, photo, follow, fans, password)
      } else{
        val updates = tBloguser.filter(_.homeurl === homeUrl).
          map(p => (p.nickname, p.homeurl, p.imageurl, p.gender,
                    p.certification, p.introduction, p.region, p.birth,
                    p.photo, p.follow, p.fans, p.password))
            .update((nickname, homeUrl, imageUrl, Some(gender.get.toString), certification,
              introduction, region, birth, photo, follow, fans, password))
        db.run(updates)
      }
    }
  }

}
