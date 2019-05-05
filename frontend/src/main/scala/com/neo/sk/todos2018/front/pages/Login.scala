package com.neo.sk.todos2018.front.pages

import com.neo.sk.todos2018.front.utils.{Http, JsFunc}
import com.neo.sk.todos2018.front.{Index, Routes}
import com.neo.sk.todos2018.shared.ptcl.SuccessRsp
import org.scalajs.dom
import org.scalajs.dom.html.Input
import com.neo.sk.todos2018.shared.ptcl.UserProtocol._
import com.neo.sk.todos2018.shared.ptcl.ToDoListProtocol._
import java.net.{URLDecoder, URLEncoder}

import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.parser._

import scala.concurrent.ExecutionContext.Implicits.global
import com.neo.sk.todos2018.front.styles.LoginStyles._
import mhtml.Var

import scala.util.{Failure, Success}
import scala.xml.Node
/**
  * Created by haoshuhan on 2018/6/4.
  */
object Login extends Index{
  //val blog = new BreakingBad("")
  def login() : Unit = {
    val username=dom.window.document.getElementById("username").asInstanceOf[Input].value
    //blog.blogName = username
    val password=dom.window.document.getElementById("password").asInstanceOf[Input].value
    val data = UserLoginReq(username, password).asJson.noSpaces
    Http.postJsonAndParse[SuccessRsp](Routes.User.login, data).map{
       case Left(error) =>
         println(s"login error: $error")
         JsFunc.alert("登录失败，请输入正确的水木账号密码！")
       case Right(rsp) =>
         if(rsp.errCode == 0) {
           JsFunc.alert("登录成功！")
           dom.window.location.hash = s"#/Blog/${username}"
         } else if(rsp.errCode == 100102){
           JsFunc.alert(s"用户名不存在!")
         } else if(rsp.errCode == 100103){
           JsFunc.alert(s"密码不正确！")
         } else {
           JsFunc.alert("登录失败，请稍后再试！")
         }

/*      case Right(rsp) =>
        if(rsp.errCode == 0) {
          JsFunc.alert("登录成功！")
          dom.window.location.hash = s"#/Blog/${username}"
        } else if(rsp.errCode == 100102){
          JsFunc.alert(s"用户名不存在!")
        } else if(rsp.errCode == 100103){
          JsFunc.alert(s"密码不正确！")
        } else {
          JsFunc.alert("登录失败，请稍后再试！")
        }

      case Left(error) =>
        println(s"login error: $error")
        JsFunc.alert("登录失败，请输入正确的水木账号密码！")*/
    }
  }

  def signup() : Unit = {
    //val username = dom.window.document.getElementById("username").asInstanceOf[Input].value
    dom.window.location.hash = s"#/Signup"
  }

  val email: Var[Node] = Var(
    <div class="form-group">
      <label for="inputEmail3" class="col-sm-3 control-label">用户名:</label>
      <div class="col-sm-9">
        <input type="text" class="form-control" id="username" value="SunTianRuo" placeholder="用户名"></input>
      </div>
    </div>
  )

  val password: Var[Node] = Var(
    <div class="form-group">
      <label for="inputPassword3" class="col-sm-3 control-label">密  码:</label>
      <div class="col-sm-9">
        <input type="password" class="form-control" id="password" value="123" placeholder="密码"></input>
      </div>
    </div>
  )

  val checkbox: Var[Node] = Var(
    <div class="form-group">
      <div class="col-sm-offset-2 col-sm-10">
        <div class="checkbox">
          <label>
            <input type="checkbox" value="Remember me"/> Remember me
          </label>
        </div>
      </div>
    </div>
  )

  val btGroup: Var[Node] = Var(
    <div class="form-group">
      <div class="col-sm-offset-2 col-sm-10">
        <button type="submit" class="btn btn-success" onclick={()=>login()}>Sign in</button>
        <button type="submit" class="btn btn-default" style="margin-left:10px" onclick={()=>signup()}>Sign up</button>
      </div>
    </div>
  )

  def render: xml.Node = {
    <div>
      <div class="heads">BreakingBad</div>
      <div class="container">
        <div class="col-md-4 col-md-offset-4" style="border:1px solid #cccccc; margin-top:15px">
          <div style="padding:2rem 2rem 2rem 2rem">
            <form class="form-horizontal">
              {email}
              {password}
              {checkbox}
              {btGroup}
            </form>
          </div>
        </div>
      </div>
    </div>
  }
}
