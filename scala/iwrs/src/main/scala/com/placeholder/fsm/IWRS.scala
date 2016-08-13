package com.placeholder.fsm

import akka.actor.FSM
import com.placeholder.app.{AppLoader, AppUtil}

class IWRS extends FSM[State, Data] {

  startWith(Born, Uninitialized)

  when(Born) {
    case Event(Load(appId), Uninitialized) =>
      println("In Born State, received message for Load app")
      val appData = AppData(AppLoader.load(appId))
      sender ! AppLoaded
      goto(Ready) using appData
  }

  when(Ready) {
    case Event(Start, AppData(appSrc)) =>
      val msg = AppUtil.firstStep(appSrc).get.logic.message
      sender ! FromAppMsg(msg)
      goto(Running)
  }

  when(Running) {
    case Event(e, s) => println("Do nothing")
      stay()
  }

  onTransition {
    case Born -> Ready =>
      println("Transitioning from Born State to Ready state")
      stateData match {
        case AppData(appSrc) => // Nothing to do
        case _                => // nothing to do
      }
  }

  whenUnhandled {
    case Event(e, s) =>
      println("received unhandled request {} in state {}/{}", e, stateName, s)
      stay
  }

//
//  when(Active, stateTimeout = 1 second) {
//    case Event(Flush | StateTimeout, t: Todo) =>
//      goto(Idle) using t.copy(queue = Vector.empty)
//  }
//
  initialize()
}
