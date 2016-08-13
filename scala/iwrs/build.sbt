name := "iwrs"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.1",
  "com.typesafe.akka" %% "akka-testkit" % "2.4.1",
  "org.scalatest" % "scalatest_2.11" % "2.2.2" % "test",
  "com.typesafe.akka" %% "akka-http-spray-json-experimental" % "2.4.9-RC2"
)
