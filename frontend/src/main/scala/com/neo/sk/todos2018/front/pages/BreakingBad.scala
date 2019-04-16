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
import mhtml._
import org.scalajs.dom.raw.HTMLElement

import scala.xml.Node

class BreakingBad(nickname: String) extends Index {
  private var currentPage = 1
  val contentList = Var(List.empty[String])
  //val urlUser = Var(List.empty[String])
  var blogName = ""

  private val pageLimit = 10
  private val pageVar = Var(1,100)
  private val pageVars = Var(1, 2, 3, 4, 5)
  private val pageList = List(1,2,3,4,5)
  var pagePoint = 1


  private var pageNum = 5
  private var gotoPage = 1

  def setContent(element: HTMLElement, str: String) = {
    element.innerHTML = str
  }

  def getContent(nickname: String): Unit = {
    val url=Routes.Blog.getContent
    val data = BlogPtcl.GetContentReq(nickname).asJson.noSpaces
    Http.postJsonAndParse[BlogPtcl.GetContentRsp](url, data).map{
      case Right(rsp) =>
        contentList := rsp.content
        //urlUser := List(rsp.urlHome)
      case Left(error) =>
        JsFunc.alert(s"请求数据失败")
        println(s"some error: $error")

    }
  }

  def getContentByPage(nickname: String, page: Int): Unit = {
    val url = Routes.Blog.getContentByPage
    val data = BlogPtcl.GetContentByPageReq(nickname, page).asJson.noSpaces
    Http.postJsonAndParse[BlogPtcl.GetContentByPageRsp](url,data).map{
      case Right(rsp) =>
        pageVar := (page, rsp.amount)
        pageNum = if(rsp.amount%pageLimit == 0) rsp.amount/pageLimit else (rsp.amount/pageLimit+1)
        contentList := rsp.content
      case Left(error) =>
        JsFunc.alert(s"请求数据失败")
        println(s"some error: $error")
    }

  }

  val left: Var[Node] = Var(
    <div class="col-xs-2" style="margin-top:15px; color:#fff; background-color:rgba(0,0,0,0.3); overflow:hidden">
      <div style="text-align:center"><br/>关注<br/>粉丝<br/>相册（待开发）</div>
      <div style="height:300px;font-size:20px;margin-top:15px;text-align:center;line-height:300px">advertisement</div>
    </div>
  )

  val publish: Var[Node] = Var(
    <div style="padding:10px 10px 10px 10px; margin-top:15px; background:#666; color:#fff">
      <div class="textarea" contenteditable="true"><br/></div>
      <input class="btn btn-danger" type="submit" value="发布" ></input>
    </div>
  )

  val contentListRx = contentList.map{
    case Nil => <div>他还没有发过博客</div>
    case list => <div>{list.map{ l =>
        <div mhtml-onmount={(e: HTMLElement) =>setContent(e, l)}>
          <hr/>
        </div>
      }
    }
    </div>
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
    if(pagePoint>pageNum-9){
      pagePoint = pageNum - 4
    }else{
      pagePoint += 5
    }
    pageVars.update{i =>
     if(i._5 >= pageNum-5){
       (pageNum-4, pageNum-3, pageNum-2, pageNum-1, pageNum)
     }
     else{
       (i._5+1, i._5+2, i._5+3, i._5+4, i._5+5)
     }
    }
  }

  val page: Var[Node] = Var(
    <div>
      <nav aria-label="Page navigation">
        <ul class="pagination">
          <li>
            <a href="javascript:void(0)" onclick={()=>previous()} aria-label="Previous" style="background-color:#cccccc;display:block">
              <span aria-hidden="true">{"<"}</span>
            </a>
          </li>
          <li><a href="javascript:void(0)" onclick={()=>getContentByPage(nickname, pagePoint)} style="background-color:#cccccc">{pageVars.map(i=>i._1)}</a></li>
          <li><a href="javascript:void(0)" onclick={()=>getContentByPage(nickname, pagePoint+1)} style="background-color:#cccccc">{pageVars.map(i=>i._2)}</a></li>
          <li><a href="javascript:void(0)" onclick={()=>getContentByPage(nickname, pagePoint+2)} style="background-color:#cccccc">{pageVars.map(i=>i._3)}</a></li>
          <li><a href="javascript:void(0)" onclick={()=>getContentByPage(nickname, pagePoint+3)} style="background-color:#cccccc">{pageVars.map(i=>i._4)}</a></li>
          <li><a href="javascript:void(0)" onclick={()=>getContentByPage(nickname, pagePoint+4)} style="background-color:#cccccc">{pageVars.map(i=>i._5)}</a></li>
          <li>
            <a href="javascript:void(0);" onclick={()=>next()} aria-label="Next" style="background-color:#cccccc">
              <span aria-hidden="true">{">"}</span>
            </a>
          </li>
        </ul>
      </nav>
    </div>
  )

  val right: Var[Node] = Var(
    <div class="col-xs-3" style="margin-top:15px;background:#666; color:#fff; text-align:center">
      <br/>姓名<br/>个人资料<br/><br/>
    </div>
  )

  val center: Var[Node] = Var(
    <div class="col-xs-7">
      {publish}
      <div style="margin-top:15px; padding:10px 10px 10px 10px; background:#666; color:#fff">
        {contentListRx}
      </div>
      {page}
    </div>
  )

  override def render: Node = {
    getContentByPage(nickname, 1)
    <div class="container" >
      <dvi style ="color:#fff">{nickname}</dvi>
      <div class="row">
        <div class="col-md-12s" style="margin-top:1px;">
          {left}
          {center}
          {right}
        </div>
      </div>
    </div>
  }
}
