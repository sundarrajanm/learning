package com.placeholder.rest

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

object Ingress extends App with IngressService {
  override implicit val system = ActorSystem("IWRS-System")
  override implicit val executor = system.dispatcher
  override implicit val materializer = ActorMaterializer()

  println("IWRS Platform is up!")
  Http().bindAndHandle(routes, config.getString("http.interface"), config.getInt("http.port"))
}