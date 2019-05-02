package com.neo.sk.todos2018.front

/**
  * Created by haoshuhan on 2018/6/4.
  */
object Routes {
  val base = "/todos2018"

  object User {
    val baseUrl = base + "/user"
    val login = baseUrl + "/userLogin"
    val logout = baseUrl + "/userLogout"
    val submit = baseUrl + "/userSubmit"
    //val back = baseUrl + "/userBack"
  }

  object List {
    val baseUrl = base + "/list"
    val getList = baseUrl + "/getList"
    val addRecord = baseUrl + "/addRecord"
    val delRecord = baseUrl + "/delRecord"
  }
  
  object BYRbbs {
    val baseUrl = base + "/byr"
    val search = baseUrl + "/kkk"
    val getArticleList = baseUrl + "/getList"
  }

  object Blog{
    val baseUrl = base + "/blog"
    val getContent = baseUrl + "/getContent"
    val getContentByPage = baseUrl + "/getContentByPage"
    val getInfo = baseUrl + "/getInfo"
    val getFansInfo = baseUrl + "/getFansInfo"
    val getFollowInfo = baseUrl + "/getFollowInfo"
    val publish = baseUrl + "/publish"
    val like = baseUrl + "/like"
    val getFollowByPage = baseUrl + "/getFollowByPage"
  }

}
