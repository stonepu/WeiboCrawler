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

class BreakingBad(nickname: String, isHome: Boolean=true) extends Index {
  val nameTemp = "考研政治徐涛"
  val contentList = Var(List.empty[BlogInfo])
                                    //content, commentUrl, like, forward, comment
  val infoList = Var(List.empty[UserInfo])
  private val pageLimit = 10
  private val pageVar = Var(1,100)
  private val pageVars = Var(1, 2, 3, 4, 5)
  private var pagePoint = 1
  private val isHomes = Var(isHome)
  private val isWatch = Var(true)

  private var show = 1 //显示种类1, 2, 3

  private var pageNum = 5

  def setContent(element: HTMLElement, str: String) = {
    element.innerHTML = str
  }

  def getContent(nickname: String): Unit = {
    val url=Routes.Blog.getContent
    val data = BlogPtcl.GetContentReq(nickname).asJson.noSpaces
    Http.postJsonAndParse[BlogPtcl.GetContentRsp](url, data).map{
      case Right(rsp) =>
        contentList := rsp.blog
        //urlUser := List(rsp.urlHome)
      case Left(error) =>
          JsFunc.alert(s"请求数据失败")
          println(s"some error: $error")

    }
  }

  def getContentByPage(nickname: String, page: Int): Unit = {
    show = 3
    val url = Routes.Blog.getContentByPage
    val data = BlogPtcl.GetContentByPageReq(nickname, page).asJson.noSpaces
    Http.postJsonAndParse[BlogPtcl.GetContentByPageRsp](url,data).map{
      case Right(rsp) =>
        pageVar := (page, rsp.amount)
        pageNum = if(rsp.amount%pageLimit == 0) rsp.amount/pageLimit else (rsp.amount/pageLimit+1)
        contentList := rsp.blog
      case Left(error) =>
        JsFunc.alert(s"请求数据失败")
        println(s"some error: $error")
    }
  }



  def publishing(): Unit = {
    val url = Routes.Blog.publish
    //val text = dom.window.document.getElementById("publishing").asInstanceOf[Input].value
    val texts = dom.window.document.getElementsByClassName("textarea")(0).textContent
    val data = BlogPtcl.PublishReq(nickname, texts).asJson.noSpaces
    if(!texts.isEmpty){
      Http.postJsonAndParse[SuccessRsp](url, data).map{
        case Right(rsp) =>
          if(rsp.errCode!=0){
            JsFunc.alert(s"发布失败!")
            println(s"error msg: ${rsp.msg}")
          }
          else
            JsFunc.alert("发布成功!")
        case Left(error) =>
          JsFunc.alert(s"发布失败!")
          println(s"some error: $error")
      }
    }
  }

  def like(commentUrl: String, like: String): Unit={
    val url = Routes.Blog.like
    val data = LikeReq(commentUrl, like).asJson.noSpaces
    Http.postJsonAndParse[SuccessRsp](url, data).map{
      case Right(rsp)=>
        if(rsp.errCode>0){
          JsFunc.alert(s"点赞失败!")
          println(s"error msg: ${rsp.msg}")
        }else
          JsFunc.alert("点赞成功!")
      case Left(error) =>
        JsFunc.alert(s"发布失败!")
        println(s"some error: $error")
    }
  }

/*  def setPosition()={
    dom.window.document.getElementById("position").scrollTop = 0
    dom.window.document.write("")
    //dom.window.addEventListener()
  }*/



  def move2Watch(nickname: String) = {
    dom.window.location.hash = s"#/move/$nickname"
  }

  def getInfo(nickname: String): Unit = {
    val url = Routes.Blog.getInfo
    val data = GetUserInfoReq(nickname).asJson.noSpaces
    Http.postJsonAndParse[GetUserInfoRsp](url, data).map{
      case Left(error)=>
        JsFunc.alert(s"请求数据失败")
        println(s"some error: $error")
      case Right(rsp) =>
        infoList := rsp.userInfo
    }
  }

  def followByPage(nickname: String, page: Int=1): Unit = {
    show = 1
    val url = Routes.Blog.getFollowByPage
    val data = GetContentByPageReq(nickname, page).asJson.noSpaces
    Http.postJsonAndParse[GetContentByPageRsp](url, data).map{
      case Left(error) =>
        JsFunc.alert(s"请求数据失败")
        println(s"some error: $error")
      case Right(rsp) =>
        contentList := rsp.blog
    }
  }

  def changePage(show: Int, nickname: String, page: Int): Unit = {
    if(show == 1) followByPage(nickname, page)
    else if(show == 3) getContentByPage(nickname, page)
  }

  val left: Var[Node] = Var(
    <div class="col-xs-2" style="margin-top:15px; color:#fff; background-color:rgba(0,0,0,0.2); overflow:hidden;">
      <div style="text-align:center; margin-top:15px">
        <div><a href={"#/"+nickname+"/follow"}><font size="4" color="white">关注</font></a></div>
        <div><a href={"#/"+nickname+"/fans"}><font size="4" color="white">粉丝</font></a></div>
        <div><font size="4" color="white">相册（待开发）</font></div>
        <div><a href="javascript:void(0)"><font size="4" color="white">回到首页()</font></a></div>
      </div>
      <div style="height:300px;font-size:20px;margin-top:15px;text-align:center;line-height:300px">advertisement</div>
      <div style="height:500px"></div>
    </div>
  )

  val publish: Var[Node] = Var(
    <div style="padding:10px 10px 10px 10px; margin-top:15px; background:#fff">
      <div class="textarea" contenteditable="true"><br/><br/></div>
      <input class="btn btn-danger" type="submit" value="发布" onclick={()=>publishing()}></input>
    </div>
  )

  def dealNickname(nickname: String): String = {
    val s = if(nickname.isEmpty) "" else s"<a href=${"\"#/move/"+nickname+"\""}><font size=${"\"3\""}>$nickname</font></a>"
    s
  }

  val contentListRx = contentList.map{
    case Nil => <div style="background:#fff">他还没有发过博客</div>
    case list => <div>{list.distinct.map{ l =>
        <div mhtml-onmount={(e: HTMLElement) =>setContent(e, dealNickname(l.author)+l.content)} style="margin-top:8px; background:#fff; padding:10px 10px 10px 10px;">
          <div>
            <button type="button" class="btn btn-link" onclick={()=>like(l.commentUrl, l.like)}>[赞{l.like}]</button>
            <button type="button" class="btn btn-link" onclick={()=>}>[评论{l.comment}]</button>
            <span>{TimeTool.dateFormatDefault(l.time)}</span>
          </div>
          <!--<hr/>-->
        </div>
      }
    }
    </div>
  }

  val isHomeRx = isHomes.map{
    case true => <div>{publish}</div>
    case false => <div></div>
    case _ => <div></div>
  }

  def previous(): Unit = {
    if(pagePoint<=5){
      pagePoint = 1
    }else{
      pagePoint -= 5
    }
    pageVars.update{
      i =>
        if(i._1<=5){
          (1, 2, 3, 4, 5)
        }
        else{
          (i._1-5, i._1-4, i._1-3, i._1-2, i._1-1)
        }
    }
  }

  def next(): Unit = {
    if(pagePoint < pageNum-4){
      if(pagePoint>pageNum-9){
        pagePoint = pageNum - 4
      }else{
        pagePoint += 5
      }
    }

    pageVars.update{i =>
      if(i._5 < pageNum){
        if(i._5 >= pageNum-5) (pageNum-4, pageNum-3, pageNum-2, pageNum-1, pageNum)
        else (i._5+1, i._5+2, i._5+3, i._5+4, i._5+5)
      }
      else (i._1, i._2, i._3, i._4, i._5)
    }
  }

  val nav: Var[Node] = Var(
    <div>
      <ul class="nav nav-tabs">
        <li role="presentation"><a href="javascript:void(0)" onclick={()=>followByPage(nickname)}>关注</a></li>
        <li role="presentation"><a href="javascript:void(0)" onclick={()=>}>可能喜欢</a></li>
        <li role="presentation"><a href="javascript:void(0)" onclick={()=>getContentByPage(nickname,1)}>我的内容</a></li>
      </ul>
    </div>
  )

  val page: Var[Node] = Var(
    <div>
      <nav aria-label="Page navigation">
        <ul class="pagination">
          <li>
            <a href="javascript:void(0)" onclick={()=>previous()} aria-label="Previous" style="display:block">
              <span aria-hidden="true">{"<"}</span>
            </a>
          </li>
          <li><a href="javascript:void(0)" onclick={()=>changePage(show, nickname, pagePoint)}>{pageVars.map(i=>i._1)}</a></li>
          <li><a href="javascript:void(0)" onclick={()=>changePage(show, nickname, pagePoint+1)}>{pageVars.map(i=>i._2)}</a></li>
          <li><a href="javascript:void(0)" onclick={()=>changePage(show, nickname, pagePoint+2)}>{pageVars.map(i=>i._3)}</a></li>
          <li><a href="javascript:void(0)" onclick={()=>changePage(show, nickname, pagePoint+3)}>{pageVars.map(i=>i._4)}</a></li>
          <li><a href="javascript:void(0)" onclick={()=>changePage(show, nickname, pagePoint+4)}>{pageVars.map(i=>i._5)}</a></li>
          <li>
            <a href="javascript:void(0);" onclick={()=>next()} aria-label="Next">
              <span aria-hidden="true">{">"}</span>
            </a>
          </li>
        </ul>
      </nav>
    </div>
  )

  val infoRx = infoList.map{ t=>
    val info = if(t.length>0) t.head else UserInfo()
    <div style="background:#fff; padding:1rem 1rem 1rem 1rem">
      <div><img src={info.imageUrl} alt="用户" class="img-circle" height="100" width="100" style="clear: both; display: block; margin:auto;"/></div>
      <div style="margin-top: 10px; text-align:center; font-size:120%; font-family:verdana; font-weight:bold">{info.nickname}</div>
      <div>性别：{info.gender}</div>
      <div>认证：{info.certification}</div>
      <div>介绍：{info.introduction}</div>
      <div>地区：{info.region}</div>
      <div>生日：{info.birth}</div>
    </div>
  }

  val right: Var[Node] = Var(
    <div class="col-xs-3" style="margin-top:15px; text-align:left;">
      {infoRx}
      <div style="height:400px; margin-top:15px; background:#fff;"><font size="4">Recommendation</font></div>
    </div>
  )

  val center: Var[Node] = Var(
    <div class="col-xs-7">
      {isHomeRx}
      <div style="margin-top:15px;">
        {nav}
      </div>
      <!--{if(isHome) {
        <div style="margin-top:15px;">
          {nav}
        </div>
        }
      }-->
      <div style="margin-top:15px;">
        {contentListRx}
      </div>
      {page}
    </div>
  )

  override def render: Node = {
    if(isHome) followByPage(nickname, 1)
    else getContentByPage(nickname, 1)
    getInfo(nickname)
    <div class="container">
      <div class="row">
        <div class="col-md-12" style="margin-top:1px;">
          <div class="row">
          {left}
          {center}
          {right}
          </div>
        </div>
      </div>
    </div>
  }
}
