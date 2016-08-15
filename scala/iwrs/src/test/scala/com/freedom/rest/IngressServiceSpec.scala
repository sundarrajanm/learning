package com.freedom.rest

import java.util.UUID

import akka.event.Logging
import akka.http.javadsl.model.headers.Location
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.typesafe.config.ConfigFactory
import org.scalatest.{FlatSpec, Matchers}

class IngressServiceSpec extends FlatSpec
  with Matchers
  with ScalatestRouteTest
  with IngressService {

  override val config = ConfigFactory.load()
  override val logger = Logging(system, getClass)

  "Ingress Service" should "respond to ping request" in {
    Get("/iwrs/ping") ~> routes ~> check {
      responseAs[String] should startWith (
        "We are alive. Have a nice day with a thought")
    }
  }

  var actorId = UUID.randomUUID()
  "Use case 1 - Order Veg Pizza" should "return actorId on app load" in {
    Post("/iwrs/app/app.json") ~> routes ~> check {
        actorId = header[Location] match {
        case Some(guid) => UUID.fromString(guid.value())
        case None => fail("Location header not found")
      }
      logger.info(s"Found actor id in response $actorId")
    }
  }

  it should "see Welcome to Pizza Hut and booking menu" in {
    Post(s"/iwrs/app/run/$actorId?cmd=start") ~> routes ~> check {
      responseAs[String] should startWith ("Welcome to Pizza Hut.")
    }
  }

  it should "select booking and see Veg or NonVeg selection" in {
    Post(s"/iwrs/app/run/$actorId?cmd=next&input=1") ~> routes ~> check {
      responseAs[String] should startWith ("Press 1 for Veg, 2 for Non-Veg.")
    }
  }

  it should "should select veg and see Veg Pizza menu" in {
    Post(s"/iwrs/app/run/$actorId?cmd=next&input=1") ~> routes ~> check {
      responseAs[String] should startWith ("Choose from the following options: 1) Exotica")
    }
  }

  it should "select option 2 and see selection confirmation" in {
    Post(s"/iwrs/app/run/$actorId?cmd=next&input=2") ~> routes ~> check {
      responseAs[String] should startWith ("Press 1 to confirm the order")
    }
  }

  it should "see confirmed Order Id" in {
    Post(s"/iwrs/app/run/$actorId?cmd=next&input=1") ~> routes ~> check {
      responseAs[String] should startWith ("Your order id is: 1244")
    }
  }

  it should "stop the conversation" in {
    Post(s"/iwrs/app/run/$actorId?cmd=stop") ~> routes ~> check {
      responseAs[String] should startWith ("Bye")
    }
  }
}
