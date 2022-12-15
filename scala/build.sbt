name := "order-shipping"

version := "1.0"

scalaVersion := "2.11.12"

libraryDependencies ++= Seq(
  "net.aichler" % "jupiter-interface" % "0.11.1" % "test",
  "com.approvaltests" % "approvaltests" % "18.5.0" % "test",
  "org.scalatest" %% "scalatest" % "3.2.3" % "test"
)
