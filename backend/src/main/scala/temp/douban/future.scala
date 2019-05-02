package temp.douban

import com.neo.sk.todos2018.models.dao.BlogDao.BlogUserDao
import com.neo.sk.todos2018.utils.TimeUtil
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.jsoup.nodes.Element

import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.io.StdIn
import scala.util.{Failure, Success}
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.util.Random

object future {
	def future1 = Future{for(i<-0 to 100000) print("A"); Thread.sleep(10)}
	def future2 = Future{for(i<-0 to 100000) print("B"); Thread.sleep(10)}
	def future3(i: Int) = {
		Future{i*i}
	}


	def dealNickname(nickname: String): String = {
		val s = if(nickname.isEmpty) "" else s"<a href=${"\"#/move/"+nickname+"\""}><font size=3>$nickname</font></a>"
		s
	}

	def main(args: Array[String]): Unit = {
		val s = "s\"dfefsf\"d"
		println(dealNickname(s))
	}
	
}
