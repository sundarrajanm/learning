package com.freedom.app

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

  val appDesign =
    """
      |{
      |  "steps": [
      |    {
      |      "step": "start",
      |      "logic": {
      |        "message": "Welcome to Pizza Hut. Enter 1 for new booking, 2 for checking booking status.",
      |        "branch": [
      |          {
      |            "value": "1",
      |            "next": "newBooking"
      |          },
      |          {
      |            "value": "2",
      |            "next": "checkStatus"
      |          }
      |        ]
      |      }
      |    },
      |    {
      |      "step": "newBooking",
      |      "logic": {
      |        "message": "Press 1 for Veg, 2 for Non-Veg.",
      |        "branch": [
      |          {
      |            "value": "1",
      |            "next": "Veg"
      |          },
      |          {
      |            "value": "2",
      |            "next": "NonVeg"
      |          }
      |        ]
      |      }
      |    },
      |    {
      |      "step": "checkStatus",
      |      "logic": {
      |        "message": "Enter your booking ID.",
      |        "next": "showStatusAndEnd"
      |      }
      |    },
      |    {
      |      "step": "showStatusAndEnd",
      |      "logic": {
      |        "message": "Your food will reach in about 30mins from now.",
      |        "next": "ThankYouAndEnd"
      |      }
      |    },
      |    {
      |      "step": "ThankYouAndEnd",
      |      "logic": {
      |        "message": "Thank you for reaching Pizza Hut. Have a nice yummy day!",
      |        "next": "stop"
      |      }
      |    },
      |    {
      |      "step": "Veg",
      |      "logic": {
      |        "message": "Choose from the following options: 1) Exotica 2) Veggie Supreme 3) Paneer Vegorama",
      |        "next": "confirmToProceed"
      |      }
      |    },
      |    {
      |      "step": "NonVeg",
      |      "logic": {
      |        "message": "Choose from the following options: 1) Chicken Italiano 2) Port Pepperoni 3) Triple Chicken Feast",
      |        "next": "confirmToProceed"
      |      }
      |    },
      |    {
      |      "step": "confirmToProceed",
      |      "logic": {
      |        "message": "Press 1 to confirm the order, 2 to change the order.",
      |        "branch": [
      |          {
      |            "value": "1",
      |            "next": "ConfirmOrderAndEnd"
      |          },
      |          {
      |            "value": "2",
      |            "next": "newBooking"
      |          }
      |        ]
      |      }
      |    },
      |    {
      |      "step": "ConfirmOrderAndEnd",
      |      "logic": {
      |        "message": "Your order id is: 1244. And your food will reach in about 30mins from now.",
      |        "next": "ThankYouAndEnd"
      |      }
      |    }
      |  ]
      |}
    """.stripMargin
}

object AppLoader extends AppJsonSupport {
  val cache = Map("app.json" -> appDesign.parseJson.convertTo[IWRSApplication])
  def load(appId: String): IWRSApplication = cache(appId)
}