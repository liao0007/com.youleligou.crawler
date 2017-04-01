name := "com.youleligou.crawler"

organization := "com.youleligou"

version := "1.0-SNAPSHOT"

scalaVersion := "2.12.1"

updateOptions := updateOptions.value.withCachedResolution(true)

val akkaVersion = "2.4.17"

val jsoupVersion = "1.8.3"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers ++= Seq(
  Resolver.typesafeRepo("release"),
  "Maven Repository" at "http://repo1.maven.org/maven2/",
  "maven-restlet" at "http://maven.restlet.org"
)

libraryDependencies ++= Seq(
//  config
  "com.typesafe" % "config" % "1.3.1",
//  logging
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
  "com.typesafe.akka" %% "akka-slf4j" % "2.5.0-RC1",
  "ch.qos.logback" % "logback-classic" % "1.1.7",
//  web client
  "com.typesafe.play" %% "play-ahc-ws-standalone" % "1.0.0-M6",
//  guice
  "net.codingwell" %% "scala-guice" % "4.1.0",
//  redis
  "com.github.etaty" %% "rediscala" % "1.8.0",
//  akka
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  //  parser
  "org.jsoup" % "jsoup" % jsoupVersion,
  "com.typesafe.play" %% "play-json" % "2.6.0-M1",
  // -- database --
  "com.typesafe.slick" %% "slick" % "3.2.0",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.2.0",
  "mysql" % "mysql-connector-java" % "5.1.40"
)
