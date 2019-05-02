package com.neo.sk.todos2018.front.pages

import com.neo.sk.todos2018.front.utils.{Http, JsFunc, TimeTool}
import com.neo.sk.todos2018.front.{Index, Routes}
import com.neo.sk.todos2018.shared.ptcl.{BlogPtcl, SuccessRsp}
import com.neo.sk.todos2018.shared.ptcl.BYRProtocol.{GetArticleReq, GetListRsp}
import io.circe.generic.auto._
import java.net.{URLDecoder, URLEncoder}

import scala.util.{Failure, Success}
import io.circe.syntax._
import io.circe.parser._

import scala.concurrent.ExecutionContext.Implicits.global
import org.scalajs.dom
import com.neo.sk.todos2018.front.styles.BYRBBSStyles._
import com.neo.sk.todos2018.front.utils.DataStore
import com.neo.sk.todos2018.shared.ptcl.BlogPtcl._
import mhtml._
import org.scalajs.dom.html.Input
import org.scalajs.dom.raw.HTMLElement

import scala.xml.Node

class Fans(nickname: String)extends Index {
  val pageLimit = 15
  val infoList = Var(List.empty[UserInfo])

  def getFansInfoByPage(nickname: String, page: Int) = {
    val url = Routes.Blog.getFansInfo
    val data = GetUserInfoByPageReq(nickname, page).asJson.noSpaces
    Http.postJsonAndParse[GetUserInfoRsp](url, data).map{
      case Left(error)=>
        JsFunc.alert(s"请求fans数据失败")
        println(s"some error: $error")
      case Right(rsp)=>
        //JsFunc.alert("请求fans数据成功")
        infoList := rsp.userInfo
    }
  }

  val infoRx = infoList.map{
    case Nil => <div>他还没有fans</div>
    case list =>
      <div>
      {list.distinct.map{info =>
      <div class="col-md-4" style="margin-top: 15px; background:#fff; height:250px; padding:1rem 1rem 1rem 1rem">
        <div><img src={info.imageUrl} alt="用户" class="img-circle" height="60" width="60" style="clear: both; display: block; margin:auto;"/></div>
        <div style="margin-top: 10px; text-align:center"><a href={s"#/move/${info.nickname}"}>{info.nickname}</a></div>
        <div>性别：{info.gender}</div>
        <div>认证：{info.certification}</div>
        <div>介绍：{info.introduction}</div>
        <div>地区：{info.region}</div>
        <div>生日：{info.birth}</div>
      </div>
        }
      }
      </div>
  }

  override def render: xml.Node = {
    getFansInfoByPage(nickname, 1)
    <div class="container">
      <div class="row">
        <div class="col-md-10 col-md-offset-1">
          {infoRx}
        </div>
      </div>
    </div>
  }
}
