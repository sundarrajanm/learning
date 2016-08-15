package com.freedom.rest

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory

object Ingress extends App with IngressService {
  override implicit val system = ActorSystem("IWRS-System")
  override implicit val executor = system.dispatcher
  override implicit val materializer = ActorMaterializer()

  override val config = ConfigFactory.load()
  override val logger = Logging(system, getClass)

  println("IWRS Platform is up!")
  Http().bindAndHandle(routes, config.getString("http.interface"), config.getInt("http.port"))
}