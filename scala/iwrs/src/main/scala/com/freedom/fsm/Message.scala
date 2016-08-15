package com.freedom.fsm

sealed trait Message

// Request messages
case class Load(appId: String) extends Message
case object Start extends Message
case object Stop extends Message
case class Next(in: String = "") extends Message

// Response messages
case object AppLoaded extends Message
case class FromAppMsg(msg: String = "") extends Message
case class ToAppMsg(msg: String) extends Message
case object Stopped extends Message