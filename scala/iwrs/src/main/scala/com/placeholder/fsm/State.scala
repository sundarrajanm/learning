package com.placeholder.fsm

sealed trait State

case object Born extends State
case object Ready extends State
case object Running extends State
case object Dead extends State




