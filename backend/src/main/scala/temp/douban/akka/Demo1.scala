package temp.douban.akka

import akka.actor.{Actor, ActorSystem, ActorRef, Props, PoisonPill}
import language.postfixOps
import akka.event.Logging
import scala.concurrent.duration._



object Demo1 extends Actor {
	val log = Logging(context.system, this)
	
	override def receive: Receive = {
		case "test" => log.info("received test")
		case  _ =>log.info("received unknown msg")
	}
}
