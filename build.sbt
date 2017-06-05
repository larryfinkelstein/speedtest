name := "speedtest"

version := "1.0"

scalaVersion := "2.12.2"
val scalaTestVersion = "3.0.3"

parallelExecution in Test := false

enablePlugins(JavaAppPackaging)

libraryDependencies ++= Seq(
  "org.scalactic" %% "scalactic" % scalaTestVersion,
    "org.scalatest" % "scalatest_2.12" % scalaTestVersion,
    "org.seleniumhq.selenium" % "selenium-java" % "3.4.0",
    "com.typesafe" % "config" % "1.3.1",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "joda-time" % "joda-time" % "2.9.9"
)

