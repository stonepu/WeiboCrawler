package com.neo.sk.todos2018.front.pages

import com.neo.sk.todos2018.front.utils.{Http, JsFunc, TimeTool}
import com.neo.sk.todos2018.front.{Index, Routes}
import com.neo.sk.todos2018.shared.ptcl.SuccessRsp
import com.neo.sk.todos2018.shared.ptcl.BYRProtocol.{GetListRsp,GetArticleReq}
import io.circe.generic.auto._
import scala.util.{Failure, Success}
import io.circe.syntax._
import io.circe.parser._

import scala.concurrent.ExecutionContext.Implicits.global
import org.scalajs.dom
import com.neo.sk.todos2018.front.styles.BYRBBSStyles._
import com.neo.sk.todos2018.front.utils.DataStore
import mhtml._

object BYRBBS extends Index {
	val boardList = Var(List.empty[(String, String)])
	val articleList = Var(List.empty[(String, String, String)])
	
	def search()={
		dom.window.location.hash=s"#/Login"
	}
	
	def getBoardList(section: String)={
		boardList := DataStore.sectionMap.filter(_._1 == section).head._2
	}
	
	def getArticleList(board: String)={
		val boards = GetArticleReq(board).asJson.noSpaces
		Http.postJsonAndParse[GetListRsp](Routes.BYRbbs.getArticleList, boards)
			.onComplete{
			case Success(aa) => aa.map{
				case rsp =>
					if(rsp.errorCode == 0){
						articleList := rsp.list.get
					}
					else {
						JsFunc.alert(rsp.msg)
						dom.window.location.hash = s"#/Login"
						println(rsp.msg)
					}
			}
			case Failure(error) =>
				println(s"get article list error: $error")
		}
	}
	
	def change2Article(content: String) = {
		//Http.postJsonAndParse()
		DataStore.artContent = content
		dom.window.location.hash = s"#/BYRbbs/article"
	}

	
	
	
	val boardListRx = boardList.map{
		case Nil => <div class={board.htmlClass}></div>
		case list => <div class={board.htmlClass}>
			<table>
				<tr>
					<th class = {th.htmlClass}>board</th>
				</tr>
				{list.map{ l =>
					<tr>
						<td class={td.htmlClass}><a style="display:block" href="javascript:void(0)" onclick={()=>getArticleList(l._2.drop(7))}>{l._1}</a></td>
					</tr>
					}
				}
			</table>
		</div>
	}
	
	
	val articleListRx = articleList.map{
		case Nil => <div class={article.htmlClass}></div>
		case list => <div class={article.htmlClass}>
			<table>
				<tr>
					<th class={th.htmlClass}>title</th>
					<th class={th.htmlClass}>author</th>
				</tr>
				{list.map{ l =>
				<tr>
					<td style="width:500px" class={td.htmlClass}><a style="display:block" href="javascript:void(0)" onclick={()=>change2Article(l._2)}>{l._1}</a></td>
					<td class={td.htmlClass}>{l._3}</td>
				</tr>
			}
				}
			</table>
		</div>
	}
	
	def app: xml.Node = {
		<div>
			<div>
				<div class={welcome.htmlClass}> <a href="https://bbs.byr.cn">BYRBBS</a></div>
			</div>
			<div style="margin-left:30px;">
				<input class={input.htmlClass} id="searching"></input>
				<button class={button.htmlClass} onclick={() => search()}>搜索</button></div>
			<div class = {section.htmlClass}>
				<button style="margin_top:30px" class={sectionButton.htmlClass} onclick={() =>getBoardList("section0")}>本站站务</button>
				<button class={sectionButton.htmlClass} onclick={() =>getBoardList("section1")}>北邮校园</button>
				<button class={sectionButton.htmlClass} onclick={() =>getBoardList("section2")}>学术科技</button>
				<button class={sectionButton.htmlClass} onclick={() =>getBoardList("section3")}>信息社会</button>
				<button class={sectionButton.htmlClass} onclick={() =>getBoardList("section4")}>人文艺术</button>
				<button class={sectionButton.htmlClass} onclick={() =>getBoardList("section5")}>生活时尚</button>
				<button class={sectionButton.htmlClass} onclick={() =>getBoardList("section6")}>休闲娱乐</button>
				<button class={sectionButton.htmlClass} onclick={() =>getBoardList("section7")}>体育健身</button>
				<button class={sectionButton.htmlClass} onclick={() =>getBoardList("section8")}>游戏对战</button>
				<button class={sectionButton.htmlClass} onclick={() =>getBoardList("section9")}>乡亲乡爱</button>
			</div>
			{boardListRx}
			{articleListRx}
		</div>
	}
}
