name := "abdullah_aleem_hw1"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.3.2",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "org.apache.httpcomponents" % "httpclient" % "4.5.10",
  "org.json4s" %% "json4s-jackson" % "3.6.7",
  "org.scalatest" %% "scalatest" % "3.0.8" % "test"
)
