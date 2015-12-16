import sbt._

object MyBuild extends Build {

  lazy val root = Project("root", file(".")) dependsOn(analyseProject)
  lazy val analyseProject =
    RootProject(uri("git://github.com/java-ocr-analyse.git"))

}