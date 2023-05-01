ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.2"

lazy val root = (project in file("."))
    .settings(
        name := "zio-web-scraper"
    )

libraryDependencies += "dev.zio" %% "zio" % "2.0.13"
libraryDependencies += "dev.zio" %% "zio-streams" % "2.0.13"
libraryDependencies += "dev.zio" %% "zio-http" % "3.0.0-RC1"
