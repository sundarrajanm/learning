package com.placeholder.fsm

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestKit, TestProbe}
import com.placeholder.app.{AppLoader, AppUtil}
import org.scalatest.{FlatSpecLike, Matchers}

class IWRSSpec extends TestKit(ActorSystem("testSystem"))
  with ImplicitSender
  with FlatSpecLike
  with Matchers {

  val iwrs = TestActorRef[IWRS]
  val iwrsActor = iwrs.underlyingActor
  val testIwrsApp = AppLoader.load("app.json")

  "Actor" should "be in Born state with Uninitialized data when instantiated" in {
    iwrsActor.stateName should be (Born)
    iwrsActor.stateData should be (Uninitialized)
  }

  it should "load the application" in {
    iwrs ! Load("app.json")
    expectMsg(AppLoaded)
    iwrsActor.stateName should be (Ready)
    iwrsActor.stateData should be (AppData(testIwrsApp))
  }

  it should "start the application and receive welcome message" in {
    iwrs ! Start
    val firstMsg = (AppUtil firstStep testIwrsApp).get.logic.message
    expectMsg(FromAppMsg(firstMsg))
    println(s"Received: $firstMsg")
    iwrsActor.stateName should be (Running)
  }

  it should "stop the FSM onreceiving Stop message" in {
    val testProbe = TestProbe()
    testProbe watch iwrs
    iwrs ! Stop
    testProbe expectTerminated (iwrs)
  }
}
