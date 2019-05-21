package com.neo.sk.todos2018.models.dao.BlogDao

import com.neo.sk.todos2018.utils.DBUtil.db
import slick.jdbc.PostgresProfile.api._
import com.neo.sk.todos2018.models.SlickTables._
import com.neo.sk.todos2018.Boot.executor
import com.neo.sk.todos2018.common.AppSettings
import org.slf4j.LoggerFactory

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.io.StdIn
import scala.collection.mutable

object BlogUserDao {
  private val log = LoggerFactory.getLogger(this.getClass)

  def addUser(nickname: Option[String]=Some(""), homeUrl: Option[String],
              imageUrl: Option[String]=Some(""), gender: Option[String]=Some(""),
              certification: Option[String]=Some(""), introduction: Option[String]=Some(""),
              region: Option[String]=Some(""), birth: Option[String]=Some(""),
              photo: Option[String]=Some(""), follow: Option[String]=Some(""),
              fans: Option[String]=Some(""), password: Option[String] = Some("123")) = {
    val addUser =
      tBloguser += rBloguser(nickname, homeUrl, imageUrl, gender, certification, introduction,
           region, birth, photo, follow, fans, password, -1)
    db.run(addUser)
  }

  def findUser(homeUrl: String) = {
    val user = tBloguser.filter(_.homeurl === homeUrl).map(_.imageurl).result
    db.run(user)
  }

  def updateUser(nickname: Option[String], homeUrl: Option[String],
                 imageUrl: Option[String], gender: Option[String],
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
                    p.photo, p.password))
            .update((nickname, homeUrl, imageUrl, Some(gender.get.toString), certification,
              introduction, region, birth, photo, password))
        db.run(updates)
      }
    }
  }

  def nickname2Url(nickname: String) = {
    val url = tBloguser.filter(_.nickname === nickname).map(p => p.homeurl).result
    db.run(url)
  }

  def getInfo(nickname: String="Non", home: String="Non") = {
    val info = tBloguser.filter(t => t.nickname === nickname || t.homeurl === home).map(p => (p.nickname, p.homeurl, p.imageurl,
    p.gender, p.certification, p.introduction, p.region, p.birth, p.photo, p.follow, p.fans)).result
    db.run(info)
  }

  def getFollow(home: String="Non", nickname: String="Non") = {
    val follow = tBloguser.filter(t => t.homeurl === home || t.nickname===nickname).map(p => p.follow).result
    db.run(follow)
  }

  def getFans(home: String="Non", nickname: String="Non") = {
    val fans = tBloguser.filter(t => t.homeurl === home || t.nickname===nickname).map(p => p.fans).result
    db.run(fans)
  }


  def updateFollow(home: String, follow: String) = {
    //println(s"====== $home 's follow 更新中")
    val user = Await.result(getFollow(home = home), Duration.Inf)
    val list = user.toList
    if(list.length==0){
      log.warn(s"follow用户 ${home} 不存在")
    } else if(list.head.getOrElse("None") == "None"){
      db.run(tBloguser.filter(_.homeurl === home).map(p => p.follow).update(Some(follow)))
    } else{
      db.run(tBloguser.filter(_.homeurl === home).map(p => p.follow).update(Some((list.head.get+follow).split("\\|").toList.distinct.mkString("|"))))
    }
  }

  def updateFans(home: String, fans: String) = {
    //println(s"====== $home 's fans 更新中")
    getFans(home = home).onComplete{ t=>
      val list = t.get.toList
      if(list.length==0){
        log.warn(s"用户 ${home} 不存在")
        db.run(tBloguser.filter(_.homeurl === home).map(p => p.fans).update(Some(fans)))
      } else if(list.head.getOrElse("None") == "None"){
        db.run(tBloguser.filter(_.homeurl === home).map(p => p.fans).update(Some(fans)))
      } else{
        db.run(tBloguser.filter(_.homeurl === home).map(p => p.fans).update(Some((list.head.get+fans).split("\\|").toList.distinct.mkString("|")+"|")))
      }
    }
  }

  def url2num(home: String) = {
    val num = tBloguser.filter(_.homeurl === home).map(p => p.u2int).result
    db.run(num)
  }

  def getUser() = {
    db.run(tBloguser.map(p => p.homeurl).result)
  }

  def dltUser(homeUrl: String) = {
    db.run(tBloguser.filter(_.homeurl === homeUrl).delete)
  }


/*
  def urlListUserInfo(nickname: String,home: List[String]) = {
    getFans(nickname = nickname)
    val info = tBloguser.filter(p => home.contains(p.homeurl)).map(p => (p.nickname, p.homeurl, p.imageurl,
      p.gender, p.certification, p.introduction, p.region, p.birth, p.photo, p.follow, p.fans)).result
    db.run(info)
  }
*/

  def main(args: Array[String]): Unit = {
    getFans(nickname = "18岁的光启Jill").map {t =>
      println(t.length)
      println(t.toList)

      //println(t.get.toList(1))
    }

   // getInfo(home="https://weibo.cn/u/6899355552")

    StdIn.readLine()
  }



}
