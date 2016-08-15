package com.placeholder.rest

import java.util.UUID

import akka.actor.{ActorSystem, Props}
import akka.event.Logging
import akka.http.scaladsl.model.headers.Location
import akka.http.scaladsl.server
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.PathMatchers.Segment
import akka.http.scaladsl.server.{Route, StandardRoute}
import akka.pattern.ask
import akka.stream.Materializer
import akka.util.Timeout
import com.placeholder.app.AppUtil
import com.placeholder.fsm._
import com.typesafe.config.ConfigFactory
import spray.json.DefaultJsonProtocol

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}
import scala.concurrent.duration._

trait Protocols extends DefaultJsonProtocol {
  //  implicit val ipInfoFormat = jsonFormat5(IpInfo.apply)
  //  implicit val ipPairSummaryRequestFormat = jsonFormat2(IpPairSummaryRequest.apply)
  //  implicit val ipPairSummaryFormat = jsonFormat3(IpPairSummary.apply)
}

trait IngressService extends Protocols {
  implicit val system: ActorSystem

  implicit def executor: ExecutionContextExecutor

  implicit val materializer: Materializer
  implicit val timeout = Timeout(5.seconds)

  val config = ConfigFactory.load()

  val logger = Logging(system, getClass)

  val routes = {
    get {
      pathPrefix("iwrs" / "ping") { handlePing }
    } ~
    post {
      (pathPrefix("iwrs" / "app" / "run") & path(Segment)) { actorId =>
        handleAppRun(actorId)
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

  def handleAppRun(actorId: String): server.Route = {
    val actorName = s"IWRS-service-$actorId"
    val tuple = for {
      actor <- system.actorSelection(s"/user/$actorName").resolveOne()
      msg <- actor ? Start
    } yield (msg, actor)

    onComplete(tuple) {
      case Success(t) =>
        t._2 ! Stop // TODO: Remove this temp code.
        t._1 match {
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

