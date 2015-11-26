import play.routes.compiler.InjectedRoutesGenerator
import play.sbt.PlayJava

name := """play-java-ocr"""

version := "0.0.1-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs
)

libraryDependencies ++= Seq(
  "be.objectify" %% "deadbolt-java" % "2.4.3"
)

//google oauth lib
libraryDependencies ++= Seq(
  "com.google.api-client" % "google-api-client" % "1.19.1",
  "com.google.apis" % "google-api-services-plus" % "v1-rev296-1.20.0",
  "com.google.apis" % "google-api-services-oauth2" % "v2-rev65-1.17.0-rc"
)

//facebook and generic oauth2 lib NOT YET PUBLISHED
/*
libraryDependencies ++= Seq(
  "com.github.scribejava" % "scribejava-apis" % "2.0"
)
*/

libraryDependencies ++= Seq(
  "org.scribe" % "scribe" % "1.3.6"
)

libraryDependencies ++= Seq(
  "postgresql" % "postgresql" % "9.1-901.jdbc4",
  "org.hibernate" % "hibernate-core" % "5.0.3.Final",
  "org.hibernate" % "hibernate-entitymanager" % "5.0.3.Final",
  "junit" % "junit" % "4.11"
)

resolvers ++= Seq(
  "webjars"    at "http://webjars.github.com/m2"
)

libraryDependencies ++= Seq(
  "org.webjars" %% "webjars-play" % "2.4.0",
  "org.webjars" % "bootstrap" % "3.1.1",
  "org.webjars" % "knockout" % "3.0.0"
)


libraryDependencies ++= Seq(
  "org.apache.chemistry.opencmis" % "chemistry-opencmis-commons-api" % "0.13.0",
  "org.apache.chemistry.opencmis" % "chemistry-opencmis-client-impl" % "0.13.0"
)
// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator


//fork in run := false