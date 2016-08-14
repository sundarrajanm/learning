package com.placeholder

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class BasicSimulation extends Simulation {

  val httpConf = http
    .baseURL("http://localhost:9000/iwrs")

  val steps = exec(http("LoadApp").post("/app/app.json")
    .check(bodyString is "AppLoaded")
    .check(header(HttpHeaderNames.Location).saveAs("actorId")))

    .pause(2)

    .exec(http("RunApp").post("/app/run/${actorId}")
      .check(regex("Welcome to Pizza Hut").ofType[String] exists))

  val scn = scenario("Basic").exec(steps)

  setUp(
    scn.inject(rampUsers(10000) over (300 seconds))
  ).protocols(httpConf).assertions(
    //global.responseTime.max.lessThan(200),
    global.responseTime.mean.lessThan(200),
    global.successfulRequests.percent.greaterThan(95)
  )
}