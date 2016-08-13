package com.placeholder.app

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._

import scala.io.Source

final case class Branch(value: String, next: String)
final case class Logic(message: String, branch: Option[List[Branch]] = None, next: Option[String] = None)
final case class Step(step: String, logic: Logic)
final case class IWRSApplication(steps: List[Step])

trait AppJsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val branchFormat = jsonFormat2(Branch)
  implicit val logicFormat = jsonFormat3(Logic)
  implicit val stepFormat = jsonFormat2(Step)
  implicit val appFormat = jsonFormat1(IWRSApplication)
}

object AppLoader extends AppJsonSupport {
  def load(appId: String): IWRSApplication = {
    val source: String = Source.fromFile(s"src/main/resources/$appId").getLines.mkString
    source.parseJson.convertTo[IWRSApplication]
  }
}
