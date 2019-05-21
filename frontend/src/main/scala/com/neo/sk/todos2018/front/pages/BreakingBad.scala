package com.neo.sk.todos2018.front.pages

import com.neo.sk.todos2018.front.utils.{Http, JsFunc, TimeTool}
import com.neo.sk.todos2018.front.{Index, Routes}
import com.neo.sk.todos2018.shared.ptcl.{BlogPtcl, SuccessRsp}
import com.neo.sk.todos2018.shared.ptcl.BYRProtocol.{GetArticleReq, GetListRsp}
import java.net.{URLDecoder, URLEncoder}

import scala.util.{Failure, Success}
import io.circe.syntax._
import io.circe.parser._
import io.circe.generic.auto._


import scala.concurrent.ExecutionContext.Implicits.global
import org.scalajs.dom
import com.neo.sk.todos2018.front.styles.BYRBBSStyles._
import com.neo.sk.todos2018.front.utils.DataStore
import com.neo.sk.todos2018.shared.ptcl.BlogPtcl._
import mhtml._
import org.scalajs.dom.html.Input
import org.scalajs.dom.raw.HTMLElement

import scala.xml.Node

class BreakingBad(home: String, isHome: Boolean=true, nickname: String="") extends Index {
  val contentList = Var(List.empty[BlogInfo])
                                    //content, commentUrl, like, forward, comment
  val infoList = Var(List.empty[UserInfo])
  val hotList = Var(List.empty[HotInfo])
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
    val data = BlogPtcl.PublishReq(home, texts).asJson.noSpaces
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

  def logout(): Unit = {
    Http.getAndParse[SuccessRsp](Routes.User.logout).map {
      case Right(rsp) =>
        if(rsp.errCode == 0) {
          JsFunc.alert("退出成功")
          dom.window.location.hash = s"#/Login"
        } else {
          JsFunc.alert("退出失败")
          println(s"logout error, ${rsp.msg}")
        }

      case Left(e) =>
        println(s"parse error,$e ")
    }
  }

  def move2Comment(comment2Int: Int): Unit = {
    dom.window.location.hash = s"#/comment/$comment2Int"
  }

  def storeBlog(commentUrl: String): Unit = {
    DataStore.commentUrl = commentUrl
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
        pageNum = if(rsp.amount%pageLimit == 0) rsp.amount/pageLimit else (rsp.amount/pageLimit+1)
    }
  }

  def changePage(show: Int, nickname: String, page: Int): Unit = {
    if(show == 1) followByPage(nickname, page)
    else if(show == 2) mayLike()
    else if(show == 3) getContentByPage(nickname, page)
    else if(show == 4) getRecommendation()
  }

  def getHot(): Unit = {
    val url = Routes.Blog.getHot
    Http.getAndParse[GetHotRsp](url).map{
      case Left(error) =>
        JsFunc.alert(s"请求数据失败")
        println(s"some error: $error")
      case Right(rsp) =>
        hotList := rsp.hotList
    }
  }

  def mayLike(): Unit = {
    show = 2
    val url = Routes.Blog.mayLike
    Http.getAndParse[GetContentByPageRsp](url).map{
      case Left(error) =>
        JsFunc.alert(s"请求数据失败")
        println(s"some error: $error")
      case Right(rsp) =>
        contentList := rsp.blog
        pageNum = 1
    }
  }

  def getRecommendation(): Unit = {
    show = 4
  }

  val left: Var[Node] = Var(
    <div class="col-xs-2" style="margin-top:15px; color:#fff; background-color:rgba(0,0,0,0.2); overflow:hidden;">
      <div style="text-align:center; margin-top:15px">
        <div><a href={"#/"+home+"/follow"}><font size="4" color="white">关注</font></a></div>
        <div><a href={"#/"+home+"/fans"}><font size="4" color="white">粉丝</font></a></div>
        <div><a href={"#/Blog/"+DataStore.home}><font size="4" color="white">回到首页</font></a></div>
        <div><a href="javascript:void(0)" onclick={()=>logout()}><font size="4" color="white">退出</font></a></div>
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

  def getCommentBtn(comment: String, commentUrl: String, comment2Int: Int) = {
    <button type="button" class="btn btn-link" onclick={()=>storeBlog(commentUrl); move2Comment(comment2Int)}>[评论{comment}]</button>
  }

  def dealNickname(nickname: String): String = {
    val s = if(nickname.isEmpty) "" else s"<a href=${"\"#/move/"+nickname+"\""}><font size=${"\"3\""}>$nickname</font></a>"
    s
  }

  val contentListRx = contentList.map{
    case Nil => <div style="background:#fff">他还没有发过博客</div>
    case list => <div>{list.distinct.map{l =>
        <div mhtml-onmount={(e: HTMLElement) =>setContent(e, dealNickname(l.author)+l.content)} style="margin-top:8px; background:#fff; padding:10px 10px 10px 10px;">
          <div>
            <button type="button" class="btn btn-link" onclick={()=>like(l.commentUrl, l.like)}>[赞{l.like}]</button>
            {getCommentBtn(l.comment, l.commentUrl, l.comment2Int)}
            <!--<button type="button" class="btn btn-link" onclick={()=>storeBlog(dealNickname(l.author)+l.content, like=l.like, comment=l.comment, commentUrl=l.commentUrl); move2Comment(l.comment2Int)}>[评论{l.comment}]</button>-->
            <span>{TimeTool.dateFormatDefault(l.time)}</span>
          </div>
        </div>
      }
    }
    </div>
  }

  val hotListRx = hotList.map{
    case Nil => <div></div>
    case list => <div style="padding:1rem 1rem 1rem 1rem">{list.distinct.map{l =>
        <div>{l.rank}
          <span><a href={l.url}>{l.title}</a></span>
          <span>{l.hotNum}</span>
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
        <li role="presentation"><a href="javascript:void(0)" onclick={()=>followByPage(home)}>关注</a></li>
        <li role="presentation"><a href="javascript:void(0)" onclick={()=>mayLike()}>平台热度排行榜</a></li>
        <li role="presentation"><a href="javascript:void(0)" onclick={()=>getContentByPage(home,1)}>我的内容</a></li>
        <li role="presentation"><a href="javascript:void(0)" onclick={()=>mayLike()}>推荐内容</a></li>
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
          <li><a href="javascript:void(0)" onclick={()=>changePage(show, home, pagePoint)}>{pageVars.map(i=>i._1)}</a></li>
          <li><a href="javascript:void(0)" onclick={()=>changePage(show, home, pagePoint+1)}>{pageVars.map(i=>i._2)}</a></li>
          <li><a href="javascript:void(0)" onclick={()=>changePage(show, home, pagePoint+2)}>{pageVars.map(i=>i._3)}</a></li>
          <li><a href="javascript:void(0)" onclick={()=>changePage(show, home, pagePoint+3)}>{pageVars.map(i=>i._4)}</a></li>
          <li><a href="javascript:void(0)" onclick={()=>changePage(show, home, pagePoint+4)}>{pageVars.map(i=>i._5)}</a></li>
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
      <div style="margin-top:15px; background:#fff;">
        {hotListRx}
      </div>
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
    if(isHome) followByPage(home, 1)
    else getContentByPage(home, 1)
    getInfo(home)
    getHot()
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
