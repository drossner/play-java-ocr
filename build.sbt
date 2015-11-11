name := """play-java-ocr"""

version := "0.0.1-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs
)

libraryDependencies += "com.google.api-client" % "google-api-client" % "1.19.1"

resolvers ++= Seq(
  "webjars"    at "http://webjars.github.com/m2"
)

libraryDependencies ++= Seq(
  "org.webjars" %% "webjars-play" % "2.4.0-1",
  "org.webjars" % "bootstrap" % "3.1.1-2",
  "org.webjars" % "knockout" % "3.0.0"
)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator


//fork in run := false