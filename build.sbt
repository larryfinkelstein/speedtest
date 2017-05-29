name := "speedtest"

version := "1.0"

scalaVersion := "2.12.2"
val scalaTestVersion = "3.0.3"

parallelExecution in Test := false

libraryDependencies ++= Seq(
  "org.scalactic" %% "scalactic" % scalaTestVersion,
  // https://mvnrepository.com/artifact/org.scalatest/scalatest_2.12
  "org.scalatest" % "scalatest_2.12" % scalaTestVersion,
  // https://mvnrepository.com/artifact/org.seleniumhq.selenium/selenium-java
  "org.seleniumhq.selenium" % "selenium-java" % "3.4.0"
)

