package com.placeholder

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class BasicSimulation extends Simulation {

  val httpConf = http
    .baseURL("http://localhost:8000/iwrs")

  val steps = exec(http("Load App").post("/app/app.json")
    .check(bodyString is "AppLoaded")
    .check(header(HttpHeaderNames.Location).saveAs("actorId")))

    .pause(2)

    .exec(http("Start App").post("/app/run/${actorId}?cmd=start")
      .check(regex("Welcome to Pizza Hut").ofType[String] exists))

    .pause(2)

    .exec(http("Select Booking").post("/app/run/${actorId}?cmd=next&input=1")
      .check(regex("Press 1 for Veg, 2 for Non-Veg.").ofType[String] exists))

    .pause(2)

    .exec(http("Select Veg").post("/app/run/${actorId}?cmd=next&input=1")
      .check(regex("Choose from the following options: 1\\) Exotica").ofType[String] exists))

    .pause(2)

    .exec(http("Select Veggie Supreme").post("/app/run/${actorId}?cmd=next&input=2")
      .check(regex("Press 1 to confirm the order").ofType[String] exists))

    .pause(2)

    .exec(http("See Order Id").post("/app/run/${actorId}?cmd=next&input=1")
      .check(regex("Your order id is: 1244").ofType[String] exists))

    .pause(2)

    .exec(http("Stop conversation").post("/app/run/${actorId}?cmd=stop")
      .check(regex("Bye").ofType[String] exists))

  val scn = scenario("Basic").exec(steps)

  setUp(
//    scn.inject(rampUsers(20) over (30 seconds))
    scn.inject(rampUsers(10 * 1000) over (300 seconds))
  ).protocols(httpConf).assertions(
    //global.responseTime.max.lessThan(200),
    //global.responseTime.mean.lessThan(200),
    global.successfulRequests.percent.greaterThan(95)
  )
}