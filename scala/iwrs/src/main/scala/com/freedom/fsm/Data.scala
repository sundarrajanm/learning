package com.freedom.fsm

import com.freedom.app.IWRSApplication

sealed trait Data
case object Uninitialized extends Data
case class AppData(val appSrc: IWRSApplication, data: Map[String, String]) extends Data