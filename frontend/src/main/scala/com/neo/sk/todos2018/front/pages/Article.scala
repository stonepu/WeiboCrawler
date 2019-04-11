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
import com.neo.sk.todos2018.front.styles.ArticleStyles._
import com.neo.sk.todos2018.front.utils.DataStore
import mhtml._

object Article extends Index {
	
	def back() ={
		dom.window.location.hash = s"#/BYRbbs"
	}
	
	
	def app:xml.Node={
		<div>
			<div><p>  {DataStore.artContent}</p>
			</div>
			<div><button class={backButton.htmlClass} onclick={()=>back()}>返回</button>
			</div>
		</div>
	}
}
