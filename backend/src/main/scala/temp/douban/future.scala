package temp.douban

import java.io.{File, FileWriter}

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
		val s = List(1,2,3,4)
		val filePath = "F:/trainning_data/user_item.txt"
		val file = new File(filePath)
		val writer = new FileWriter(file, true)
		s.foreach{t =>
			writer.write(t + "\r\n")
		}
		writer.close()
	}
	
}
