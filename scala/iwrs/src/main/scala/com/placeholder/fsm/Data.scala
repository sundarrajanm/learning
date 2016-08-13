package com.placeholder.fsm

import com.placeholder.app.IWRSApplication

sealed trait Data
case object Uninitialized extends Data
case class AppData(val appSrc: IWRSApplication) extends Data