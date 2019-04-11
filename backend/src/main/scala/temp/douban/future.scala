package temp.douban

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
	def main(args: Array[String]): Unit = {
		//Future{for(i<-0 to 100000) print("A"); Thread.sleep(10)}
		//Future{for(i<-0 to 500000) print("B"); println("");Thread.sleep(10)}
		//for{n1<- future2; n2<- future1} null
		/*val f = Future{
			42
		}
		val f2 = Future{
			33
		}
		future2.onComplete{
			case Success(i) =>
				for(i<-0 to 50000)print("*")
				future1.onComplete{
					case Success(t) => null
					case Failure(error) => println(s"error: $error")
				}
			case Failure(s) => println(s)
		}*/
		
//		(1 to 10).foreach( i =>
//		 println(i))
		
		val s = "1"
    println(s.toInt)


	}
	
}
