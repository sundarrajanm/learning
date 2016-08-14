package com.placeholder.fsm

import akka.actor.FSM
import com.placeholder.app.{AppLoader, AppUtil}

class IWRS extends FSM[State, Data] {

  startWith(Born, Uninitialized)

  when(Born) {
    case Event(Load(appId), Uninitialized) =>
      val appData = AppData(AppLoader.load(appId))
      sender ! AppLoaded
      goto(Ready) using appData
  }

  when(Ready) {
    case Event(Start, AppData(appSrc)) =>
      val msg = AppUtil.firstStep(appSrc).get.logic.message
      log.info(s"Received Start and responding with ${msg}")
      sender ! FromAppMsg(msg)
      goto(Running)
  }

  when(Running) {
    case Event(Stop, _) => stop(FSM.Normal)
    case Event(e, s) => stay()
  }

  whenUnhandled {
    case Event(e, s) =>
      log.error("received unhandled request {} in state {}/{}", e, stateName, s)
      stop(FSM.Shutdown)
  }

  initialize()

  override def postStop(): Unit = {
    super.postStop()
    log.info(s"Stopping actor ${self.path.name}")
  }
}
