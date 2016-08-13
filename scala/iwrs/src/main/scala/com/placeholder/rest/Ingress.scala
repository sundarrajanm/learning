import akka.actor.{ActorSystem, Props}
import akka.pattern.{ask, pipe}
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.{ActorMaterializer, Materializer}
import akka.stream.scaladsl.{Flow, Sink, Source}
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import java.io.IOException
import java.util.UUID

import akka.http.scaladsl.model.headers.Location
import akka.util.Timeout
import com.placeholder.fsm.{IWRS, Load, Start}

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.math._
import spray.json.DefaultJsonProtocol

import scala.concurrent.duration._
import scala.util.{Failure, Success}

trait Protocols extends DefaultJsonProtocol {
//  implicit val ipInfoFormat = jsonFormat5(IpInfo.apply)
//  implicit val ipPairSummaryRequestFormat = jsonFormat2(IpPairSummaryRequest.apply)
//  implicit val ipPairSummaryFormat = jsonFormat3(IpPairSummary.apply)
}

trait Service extends Protocols {
  implicit val system: ActorSystem
  implicit def executor: ExecutionContextExecutor
  implicit val materializer: Materializer
  implicit val timeout = Timeout(5 seconds)

  def config: Config

  val logger: LoggingAdapter

  val routes = {
    post {
      (pathPrefix("iwrs" / "app" / "run") & path(Segment)) { actorId =>
        val actorName = s"IWRS-service-$actorId"
        val msg = for {
          actor <- system.actorSelection(s"/user/$actorName").resolveOne()
          msg <- actor ? Start
        } yield msg

        onComplete(msg) {
          case Success(m) => complete(m.toString)
          case Failure(e) => complete(500, e)
        }
      } ~ (pathPrefix("iwrs" / "app") & path(Segment)) { appId =>
        val actorId = UUID.randomUUID().toString
        val actorName = s"IWRS-service-$actorId"
        val actorRef = system.actorOf(Props[IWRS], actorName)

        println(s"actor path : ${actorRef.path}")
        onComplete(actorRef ? Load(appId)) {
          case Success(msg) => respondWithHeader(Location(actorId)) {
            complete(msg.toString)
          }
          case Failure(x) => complete(500, "Error")
        }
      }
    }
  }
}

object Ingress extends App with Service {
  override implicit val system = ActorSystem("IWRS-System")
  override implicit val executor = system.dispatcher
  override implicit val materializer = ActorMaterializer()

  override val config = ConfigFactory.load()
  override val logger = Logging(system, getClass)

  Http().bindAndHandle(routes, config.getString("http.interface"), config.getInt("http.port"))
}