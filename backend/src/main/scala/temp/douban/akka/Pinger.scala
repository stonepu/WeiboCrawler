package temp.douban.akka

import akka.actor.{Actor, ActorSystem, ActorRef, Props, PoisonPill}
import language.postfixOps
import akka.event.Logging
import scala.concurrent.duration._
import scala.io.StdIn

case object Ping
case object Pong



class Pinger extends Actor {
	var countDown = 100
	
	override def receive: Receive = {
		case Pong =>
			println(s"${self.path} received pong, count down $countDown")
			if(countDown>0) {
				countDown -= 1
				sender() ! Ping
			} else {
				sender() ! PoisonPill
				self ! PoisonPill
			}
	}
}

class Ponger(pinger: ActorRef) extends Actor {
	override def receive: Receive = {
		case Ping =>
			println(s"${self.path} received ping")
			pinger ! Pong
	}
}

object Demo2 {
	val system = ActorSystem("pingpong")
	val pinger = system.actorOf(Props[Pinger], "pinger")
	val ponger = system.actorOf(Props(classOf[Ponger], pinger), "ponger")
	
	//val props1 = Props
	//val props2 = Props(new ActorWithArgs(""))
	//val props3 = Props(classof)
	
	import system.dispatcher
	class Argument(val value: String) extends AnyVal
	class ValueClassActor(arg: Argument) extends Actor {
		override def receive: Receive = {case _ => ()}
	}
	
	object ValueClassActor {
		def props2(arg: Argument) = Props(classOf[ValueClassActor], arg.value)
		def props3(arg: Argument) = Props(new ValueClassActor(arg))
	}
	
	def main(args: Array[String]): Unit = {
		system.scheduler.scheduleOnce(5000 millis) {
			ponger ! Ping
		}
		val s = StdIn.readLine()
	}
}
