package com.freedom.rest

import java.util.UUID

import akka.actor.{ActorSystem, Props}
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.model.headers.Location
import akka.http.scaladsl.server
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.PathMatchers.Segment
import akka.http.scaladsl.server.{Route, StandardRoute}
import akka.http.scaladsl.unmarshalling.Unmarshaller
import akka.pattern.ask
import akka.stream.Materializer
import akka.util.Timeout
import com.freedom.app.AppUtil
import com.freedom.fsm._
import com.typesafe.config.{Config, ConfigFactory}
import spray.json.DefaultJsonProtocol

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}
import scala.concurrent.duration._

trait IngresPayload extends DefaultJsonProtocol {
  implicit val cmdTypeUnmarshaller: Unmarshaller[String, Message] =
    Unmarshaller.strict[String, Message] { cmd =>
      cmd.toLowerCase match {
        case "start" => Start
        case "next"  => Next()
        case "stop"  => Stop
      }
    }
}

trait IngressService extends IngresPayload {
  implicit val system: ActorSystem

  implicit def executor: ExecutionContextExecutor

  implicit val materializer: Materializer
  implicit val timeout = Timeout(5.seconds)

  def config: Config

  val logger: LoggingAdapter

  val routes = {
    get {
      pathPrefix("iwrs" / "ping") { handlePing }
    } ~
    post {
      (pathPrefix("iwrs" / "app" / "run") & path(Segment) &
        parameters("cmd".as[Message], "input".?)) { (actorId, cmd, input) =>
        handleAppRun(actorId, cmd, input)
      } ~ (pathPrefix("iwrs" / "app") & path(Segment)) { appId =>
        handleAppStart(appId)
      }
    }
  }

  def handleAppStart(appId: String): Route = {
    val actorId = UUID.randomUUID().toString
    val actorName = s"IWRS-service-$actorId"
    val actorRef = system.actorOf(Props[IWRS], actorName)

    logger.info(s"Actor launched: ${actorRef.path}")
    onComplete(actorRef ? Load(appId)) {
      case Success(msg) => respondWithHeader(Location(actorId)) {
        complete(msg.toString)
      }
      case Failure(x) => complete(500, "Error")
    }
  }

  def handleAppRun(actorId: String, cmd: Message, input: Option[String]): server.Route = {
    val actorName = s"IWRS-service-$actorId"

    val procCmd = cmd match {
      case Next(_) => Next(input.getOrElse(""))
      case _ => cmd
    }

    val msg = for {
      actor <- system.actorSelection(s"/user/$actorName").resolveOne()
      msg <- actor ? procCmd
    } yield msg

    onComplete(msg) {
      case Success(res) =>
        res match {
          case FromAppMsg(msg) => complete(msg)
          case _ => complete("")
        }
      case Failure(e) => complete(500, e)
    }
  }

  def handlePing: StandardRoute = {
    complete(s"We are alive. Have a nice day with a thought: ${AppUtil.randomQuote}")
  }
}

