lazy val commonSettings = Seq(
  organization := "jwt-moat",
  version := "0.1.0",
  crossScalaVersions := Seq("2.10.5", "2.11.6"),
  scalaVersion := "2.11.6"
)

lazy val core = (project in file("core")).
  settings(commonSettings: _*).
  settings(
    libraryDependencies ++= Seq(
      "org.bitbucket.b_c" % "jose4j" % "0.4.1",
      "org.scalatest" %% "scalatest" % "2.2.4" % "test",
      "org.slf4j" % "slf4j-api" % "1.7.12",
      "io.jsonwebtoken" % "jjwt" % "0.4",
      "org.slf4j" % "slf4j-simple" % "1.7.12" % "test"
    )
  )
