package com.neo.sk.todos2018.front

import cats.Show
import com.neo.sk.todos2018.front.pages._
import mhtml.mount
import org.scalajs.dom
import com.neo.sk.todos2018.front.utils.{Http, JsFunc, PageSwitcher}
import mhtml._
import org.scalajs.dom
import io.circe.syntax._
import io.circe.generic.auto._
import com.neo.sk.todos2018.front.styles._
/**
  * Created by haoshuhan on 2018/6/4.
  */
object Main extends PageSwitcher {
  val currentPage = currentHashVar.map { ls =>
    println(s"currentPage change to ${ls.mkString(",")}")
    ls match {
      case "Login" :: Nil => Login.app
      case "List" :: username :: Nil => new TaskList(username).app
      case "Signup" :: Nil => Signup.app
      case "BYRbbs" :: Nil => BYRBBS.app
      case "BYRbbs"::"article" :: Nil => Article.app
      case _ => BreakingBad.app
    }
  }

  def show(): Cancelable = {
    switchPageByHash()
    val page =
      <div>
        {currentPage}
      </div>
    mount(dom.document.body, page)
  }


  def main(args: Array[String]): Unit ={
    import scalacss.ProdDefaults._
    LoginStyles.addToDocument()
    ListStyles.addToDocument()
    SignupStyles.addToDocument()
    BYRBBSStyles.addToDocument()
    ArticleStyles.addToDocument()
    show()
  }
}
