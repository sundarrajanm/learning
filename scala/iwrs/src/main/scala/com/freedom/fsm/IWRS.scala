package com.freedom.fsm

import akka.actor.FSM
import com.freedom.app._

class IWRS extends FSM[State, Data] {

  // TODO: Constants below move it out later.
  val CURRENT_STEP = "CURRENT_STEP"

  startWith(Born, Uninitialized)

  when(Born) {
    case Event(Load(appId), Uninitialized) =>
      val iwrsSrc = AppLoader.load(appId)
      sender ! AppLoaded
      goto(Ready) using AppData(iwrsSrc, Map())
  }

  when(Ready) {
    case Event(Start, AppData(appSrc, data)) =>
      val step1 = AppUtil.firstStep(appSrc).get

      log.info(s"Starting the app and responding with: ${step1.logic.message}")
      sender ! FromAppMsg(step1.logic.message)
      val newAppData = AppData(appSrc, data.updated(CURRENT_STEP, step1.step))
      goto(Running) using newAppData
  }

  when(Running) {
    case Event(Stop, _) =>
      sender ! FromAppMsg("Bye")
      stop(FSM.Normal)
    case Event(Next(input), currentData: AppData) =>
      log.info(s"Received Next message while on current step: ${currentData.data(CURRENT_STEP)}")
      val newAppData: AppData = findNextStep(currentData.appSrc, currentData.data(CURRENT_STEP), input) match {
        case Some(s) =>
          sender ! FromAppMsg(s.logic.message)
          AppData(currentData.appSrc, currentData.data.updated(CURRENT_STEP, s.step))
        case None => currentData
      }
      stay() using newAppData
  }

  def findNextStep(iwrsSrc: IWRSApplication, currentStep: String, input: String): Option[Step] = {
    val cStep = iwrsSrc.steps.find(s => s.step equalsIgnoreCase currentStep)
    val cBranches = cStep flatMap (s => s.logic.branch)

    val nextStepName: Option[String] =
      if (cBranches.isDefined)
        cBranches match {
          case Some(b) =>
            val nBranch = b find (_.value equalsIgnoreCase input)
            nBranch map (_.next)
          case None => None
        }
    else
      cStep flatMap (s => s.logic.next)

    nextStepName.flatMap(n => iwrsSrc.steps.find(s => s.step equalsIgnoreCase(n)))
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
