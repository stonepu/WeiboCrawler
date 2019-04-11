package com.neo.sk.todos2018.front.pages

import com.neo.sk.todos2018.front.utils.{Http, JsFunc}
import com.neo.sk.todos2018.front.{Index, Routes}
import com.neo.sk.todos2018.shared.ptcl.SuccessRsp
import org.scalajs.dom
import org.scalajs.dom.html.Input
import com.neo.sk.todos2018.shared.ptcl.UserProtocol._
import com.neo.sk.todos2018.shared.ptcl.ToDoListProtocol._
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.parser._

import scala.concurrent.ExecutionContext.Implicits.global
import com.neo.sk.todos2018.front.styles.SignupStyles._
import mhtml.Var

import scala.xml.Node
//import jdk.internal.util.xml.impl.Input

object Signup extends Index {
  def submit() : Unit = {
    val username=dom.window.document.getElementById("username").asInstanceOf[Input].value
    val password=dom.window.document.getElementById("password").asInstanceOf[Input].value
    val passwordAgain=dom.window.document.getElementById("passwordAgain").asInstanceOf[Input].value
    val data = UserSignupReq(username, password, passwordAgain).asJson.noSpaces
    Http.postJsonAndParse[SuccessRsp](Routes.User.submit, data).map {
      case Right(rsp) =>
        if(rsp.errCode == 0) {
          JsFunc.alert("注册成功！")
          dom.window.location.hash = s"#/Login"
        }else if (rsp.errCode == 100113){
          JsFunc.alert("用户名已经存在！")
        }else if(rsp.errCode == 100114){
          JsFunc.alert("确人密码不一致，请重新输入！")
        }else{
          JsFunc.alert("注册失败，请稍后再试！")
        }
      case Left(error) =>
        println(s"Signup error: $error")
        JsFunc.alert("注册失败leya！")
    }
  }

  def back() : Unit = {
    dom.window.location.hash = s"#/Login"
  }

  val email: Var[Node] = Var(
    <div class="form-group">
      <label for="inputEmail3" class="col-sm-4 control-label">用 户 名:</label>
      <div class="col-sm-8">
        <input type="text" class="form-control" id="username" placeholder="用户名"></input>
      </div>
    </div>
  )

  val password: Var[Node] = Var(
    <div class="form-group">
      <label for="inputPassword3" class="col-sm-4 control-label">密    码:</label>
      <div class="col-sm-8">
        <input type="password" class="form-control" id="password" placeholder="密码"></input>
      </div>
    </div>
  )

  val passwordSure: Var[Node] = Var(
    <div class="form-group">
      <label for="inputPassword3" class="col-sm-4 control-label">确认密码:</label>
      <div class="col-sm-8">
        <input type="password" class="form-control" id="passwordAgain" placeholder="确认密码"></input>
      </div>
    </div>
  )

  val btGroup: Var[Node] = Var(
    <div class="form-group">
      <div class="col-sm-offset-2 col-sm-10">
        <button type="submit" class="btn btn-success" onclick={()=>submit()}>Sign up</button>
        <button type="submit" class="btn btn-default" style="margin-left:10px" onclick={()=>back()}>Back</button>
      </div>
    </div>
  )

  def app : xml.Node = {
    <div>
      <div class="heads">BreakingBad</div>
      <div class="container">
        <div class="col-md-4 col-md-offset-4" style="border:1px solid #cccccc; margin-top:15px">
          <div style="padding:2rem 3rem 1rem 1rem">
            <form class="form-horizontal">
              {email}
              {password}
              {passwordSure}
              {btGroup}
            </form>
          </div>
        </div>
      </div>
    </div>
  }
}
