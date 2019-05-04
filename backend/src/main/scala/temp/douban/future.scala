package temp.douban

import com.neo.sk.todos2018.models.dao.BlogDao.BlogUserDao
import com.neo.sk.todos2018.shared.ptcl.BlogPtcl.{GetContentByPageReq, MatrixElement}
import com.neo.sk.todos2018.utils.TimeUtil
import io.circe.{Decoder, Json}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.jsoup.nodes.Element
import io.circe.syntax._
import io.circe.parser._
import io.circe.generic.auto._

import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.io.StdIn
import scala.util.{Failure, Success}
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.util.Random
import scala.util.parsing.json.JSON

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

	def regJson(json: Option[Any]) = json match {
		case Some(map: Map[String, Map[String, Int]]) => map
		/*case None => "error"
		case other => "Unknow data structure : " + other*/
	}

	def main(args: Array[String]): Unit = {
		val s = "{\"0\":{\"item\":1,\"score\":1,\"time\":111,\"user\":1},\"1\":{\"item\":2,\"score\":3,\"time\":123,\"user\":1}}"
		val jsonS = JSON.parseFull(s)
		val sdata = s.asJson
		val stats = parse(s)
		val ss = regJson(jsonS)
		val list = ListBuffer[(Int, Int, Int)]()
		ss.foreach(t => list +=((t._2("user"), t._2("item"), t._2("score"))))
		println(list)
		//Decoder(stats)
	}
	
}
