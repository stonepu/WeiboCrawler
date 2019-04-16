package com.neo.sk.todos2018.front

import cats.Show
import com.neo.sk.todos2018.front.pages._
import mhtml.mount
import org.scalajs.dom
import com.neo.sk.todos2018.front.utils.{Http, JsFunc, PageSwitcher}
import mhtml._
import java.net.{URLDecoder, URLEncoder}
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
      case "Login" :: Nil => Login.render
      case "List" :: username :: Nil => new TaskList(username).render
      case "Signup" :: Nil => Signup.render
      case "BYRbbs" :: Nil => BYRBBS.render
      case "BYRbbs"::"article" :: Nil => Article.render
      case "Blog":: nickname :: Nil => new BreakingBad(URLDecoder.decode(nickname)).render
      case _ => Login.render
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
