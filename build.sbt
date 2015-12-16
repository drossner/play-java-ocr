import play.routes.compiler.InjectedRoutesGenerator
import play.sbt.PlayJava

name := """play-java-ocr"""

version := "0.0.1-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  evolutions
)
//user and role management
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

//Image stuff
libraryDependencies ++= Seq(
  "org.imgscalr" % "imgscalr-lib" % "4.2",
  "com.twelvemonkeys.imageio" % "imageio-core" % "3.2",
  "com.twelvemonkeys.common" % "common-lang" % "3.2",
  "com.twelvemonkeys.imageio" % "imageio-tiff" % "3.2"
)

//Persistence
libraryDependencies ++= Seq(
  "postgresql" % "postgresql" % "9.1-901.jdbc4",
  "org.hibernate" % "hibernate-core" % "5.0.3.Final",
  "org.hibernate" % "hibernate-entitymanager" % "5.0.3.Final",
  javaJpa
)

//Export
libraryDependencies ++= Seq{
  "org.docx4j" % "docx4j" % "3.2.2"
}

//Junit test
libraryDependencies += "junit" % "junit" % "4.11"

//Webjars (Client source in jars)
resolvers ++= Seq(
  "webjars"    at "http://webjars.github.com/m2"
)

//client libs
libraryDependencies ++= Seq(
  "org.webjars" %% "webjars-play" % "2.4.0",
  "org.webjars" % "bootstrap" % "3.3.5",
  "org.webjars" % "knockout" % "3.0.0"
)

// not public
// git clone https://github.com/frudisch/java-ocr-analyse.git
// (install sbt)
// use sbt publishLocal fot the project above
// now the following dependency can be resolved
libraryDependencies += "com.practicum.web.architecture.ocr" % "java-ocr-analyse" % "1.0.0"

libraryDependencies ++= Seq(
  "org.apache.chemistry.opencmis" % "chemistry-opencmis-commons-api" % "0.13.0",
  "org.apache.chemistry.opencmis" % "chemistry-opencmis-client-impl" % "0.13.0"
)
// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator


//fork in run := false