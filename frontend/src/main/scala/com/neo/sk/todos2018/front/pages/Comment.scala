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
import com.neo.sk.todos2018.front.utils.DataStore
import com.neo.sk.todos2018.shared.ptcl.BlogPtcl._
import mhtml._
import org.scalajs.dom.html.Input
import org.scalajs.dom.raw.HTMLElement

import scala.collection.mutable.ListBuffer
import scala.xml.Node

class Comment(comment2Int: Int) extends Index {
  val commentList = Var(List.empty[CommentInfo])
  val blog = Var(List.empty[BlogInfo])

  def getComment(): Unit = {
    val url = Routes.Blog.getComment
    val data = GetCommentReq(DataStore.commentUrl).asJson.noSpaces
    Http.postJsonAndParse[GetCommentRsp](url, data).map{
      case Left(error) =>
        JsFunc.alert(s"请求数据失败")
        println(s"some error: $error")
      case Right(rsp) =>
        commentList := rsp.commentList
    }
  }

  def getArt(): Unit = {
    val url = Routes.Blog.getArticle
    val data = GetContentReq(commentUrl=DataStore.commentUrl).asJson.noSpaces
    Http.postJsonAndParse[GetContentRsp](url, data).map{
      case Left(error) =>
        JsFunc.alert(s"请求数据失败")
        println(s"some error: $error")
      case Right(rsp) =>
        blog := rsp.blog
    }

  }

  def setContent(element: HTMLElement, str: String) = {
    element.innerHTML = str
  }

  val blogRx = blog.map{
    case Nil => <div></div>
    case list =>
      <div mhtml-onmount={(e: HTMLElement) =>setContent(e, list.head.content)} style="margin-top:8px; background:#fff; padding:10px 10px 10px 10px;">
        <div>{DataStore.commentUrl}</div>
      </div>
  }

  val commentListRx = commentList.map{
    case Nil => <div>还没有评论</div>
    case list => <div>{
      <ul class="list-group">
        {list.map { l =>
        <li class="list-group-item">
          {l.reviewer + "回复：" + l.reviewed +" "+ l.content + TimeTool.dateFormatDefault(l.time)}
        </li>
          }
        }
      </ul>
    }
    </div>
  }

  override def render: Node = {
    getArt()
    getComment()
    <div class="container">
      <div>
        {blogRx}
        {commentListRx}
      </div>
    </div>
  }
}
