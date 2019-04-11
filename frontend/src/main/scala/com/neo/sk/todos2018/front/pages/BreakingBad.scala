package com.neo.sk.todos2018.front.pages

import com.neo.sk.todos2018.front.utils.{Http, JsFunc, TimeTool}
import com.neo.sk.todos2018.front.{Index, Routes}
import com.neo.sk.todos2018.shared.ptcl.SuccessRsp
import com.neo.sk.todos2018.shared.ptcl.BYRProtocol.{GetArticleReq, GetListRsp}
import io.circe.generic.auto._

import scala.util.{Failure, Success}
import io.circe.syntax._
import io.circe.parser._

import scala.concurrent.ExecutionContext.Implicits.global
import org.scalajs.dom
import com.neo.sk.todos2018.front.styles.BYRBBSStyles._
import com.neo.sk.todos2018.front.utils.DataStore
import mhtml._

import scala.xml.Node

object BreakingBad extends Index {
  override def app: Node = {
    <div class="container">
      <div style="height:40px; text-align:center; background:#F00; color:#FFF;">breaking bad</div>
    </div>
  }
}
