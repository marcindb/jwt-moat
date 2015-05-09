lazy val commonSettings = Seq(
  organization := "io.moat",
  version := "0.1.0",
  crossScalaVersions := Seq("2.10.5", "2.11.6"),
  scalaVersion := "2.11.6"
)

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  aggregate(core, spray, play)

lazy val core = (project in file("core")).
  settings(commonSettings: _*).
  settings(
    moduleName := "jwt-moat-core",
    libraryDependencies ++= Seq(
      "org.bitbucket.b_c" % "jose4j" % "0.4.1",
      "org.scalatest" %% "scalatest" % "2.2.4" % "test",
      "org.slf4j" % "slf4j-api" % "1.7.12",
      "io.jsonwebtoken" % "jjwt" % "0.4",
      "org.slf4j" % "slf4j-simple" % "1.7.12" % "test"
    )
  )

val sprayVersion = System.getProperty("spray.version", "1.3.3")

val playVersion = System.getProperty("play.version", "2.3.7")

lazy val spray = (project in file("spray")).
  dependsOn(core).
  settings(commonSettings: _*).
  settings(
    moduleName := "jwt-moat-spray",
    libraryDependencies ++= Seq(
      "io.spray" %% "spray-routing" % sprayVersion % "provided"
    )
  )

lazy val play = (project in file("play")).
  dependsOn(core).
  settings(commonSettings: _*).
  settings(
    moduleName := "jwt-moat-play",
    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play" % playVersion % "provided"
    ),
    resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
  )
