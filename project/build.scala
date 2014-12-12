import sbt._
import sbt.Keys._
object Build extends sbt.Build {

  lazy val basicSettings = Seq(
    organization := "com.yiguang",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.10.4",
    //finagle not support scala 2.11
    //crossScalaVersions := Seq("2.10.4", "2.11.4"),
    resolvers ++= Seq(
      "twitter.com" at "http://maven.twttr.com/"
    )
  )

  lazy val root = Project("memcachedb",file("."))
    .settings(basicSettings:_*)
    .settings(libraryDependencies ++= Dependencies.all)
    .settings(XitrumPackage.copy("bin","conf","log"):_*)
}

object Dependencies {
  val logback       = "ch.qos.logback" % "logback-classic" % "1.1.1"
  val slf4s         = "org.slf4s" %% "slf4s-api" % "1.7.6"
  val finagle_memcached  = "com.twitter" %% "finagle-memcached" % "6.22.0" exclude("org.slf4j","slf4j-jdk14")
  val scala_test    = "org.scalatest" %% "scalatest" % "2.2.1"
  val xmemcached    = "com.googlecode.xmemcached" % "xmemcached" % "2.0.0" % "test"
  val leveldbjni    = "org.fusesource.leveldbjni" % "leveldbjni-linux64" % "1.8"
  val akka_actor    = "com.typesafe.akka" %% "akka-actor" % "2.3.7"

  val all = Seq(logback,slf4s,finagle_memcached,scala_test,xmemcached,leveldbjni,akka_actor)
}
