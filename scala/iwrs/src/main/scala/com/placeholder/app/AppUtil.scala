package com.placeholder.app

import scala.io.Source
import scala.util.Random


object AppUtil {
  def firstStep(app: IWRSApplication) = app.steps find (s => s.step equalsIgnoreCase "start")
  val pingQuotes = Source.fromFile("src/main/resources/pingQuotes.txt").getLines.toList
  def randomQuote = pingQuotes(Random.nextInt(pingQuotes.length))
}
