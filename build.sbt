lazy val commonSettings = Seq(
  organization := "io.moat",
  version := "0.1.0",
  crossScalaVersions := Seq("2.10.5", "2.11.6"),
  scalaVersion := "2.10.5"
)

val commonDependencies = Seq(
  "org.slf4j" % "slf4j-api" % "1.7.12"
)

val testDependencies = Seq(
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "org.slf4j" % "slf4j-simple" % "1.7.12" % "test"
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
      "io.jsonwebtoken" % "jjwt" % "0.4"
    ) ++ commonDependencies ++ testDependencies
  )

lazy val spray = (project in file("spray")).
  dependsOn(core).
  settings(commonSettings: _*).
  settings(
    moduleName := "jwt-moat-spray",
    libraryDependencies ++= {
      val akkaVersion = "2.3.9"
      val sprayVersion = System.getProperty("spray.version", "1.3.3")
      Seq(
        "io.spray" %% "spray-can" % sprayVersion,
        "io.spray" %% "spray-routing" % sprayVersion,
        "io.spray" %% "spray-testkit" % sprayVersion % "test",
        "com.typesafe.akka" %% "akka-actor" % akkaVersion,
        "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test"
      ) ++ commonDependencies ++ testDependencies
    }
  )

lazy val play = (project in file("play")).
  dependsOn(core).
  settings(commonSettings: _*).
  settings(
    moduleName := "jwt-moat-play",
    libraryDependencies ++= {
      val playVersion = System.getProperty("play.version", "2.3.7")
      Seq(
      "com.typesafe.play" %% "play" % playVersion % "provided"
      ) ++ commonDependencies ++ testDependencies
    },
    resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
  )
