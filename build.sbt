name := "com.youleligou.crawler"

organization := "com.youleligou"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.8"

updateOptions := updateOptions.value.withCachedResolution(true)

val akkaVersion    = "2.5.0"
val jsoupVersion   = "1.8.3"
val phantomVersion = "2.7.0"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers ++= Seq(
  Resolver.typesafeRepo("release"),
  "Maven Repository" at "http://repo1.maven.org/maven2/",
  "maven-restlet" at "http://maven.restlet.org"
)

libraryDependencies ++= Seq(
  // --- config ---
  "com.typesafe"      % "config"       % "1.3.1",
  "com.github.andr83" %% "scalaconfig" % "0.3",

  // --- logging ---
  "com.typesafe.scala-logging" %% "scala-logging"  % "3.5.0",
  "com.typesafe.akka"          %% "akka-slf4j"     % "2.5.0-RC1",
  "ch.qos.logback"             % "logback-classic" % "1.1.7",

  // --- webclient ---
  "com.typesafe.play" %% "play-ahc-ws-standalone" % "1.0.0-M6",

  // --- guice ---
  "net.codingwell" %% "scala-guice" % "4.1.0",

  // --- redis ---
  "com.github.etaty" %% "rediscala" % "1.8.0",

  // --- akka ---
  "com.typesafe.akka" %% "akka-actor"            % akkaVersion,
  "com.typesafe.akka" %% "akka-remote"           % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-tools"    % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion,
  "com.typesafe.akka" %% "akka-persistence"      % akkaVersion,
  "com.typesafe.akka" %% "akka-contrib"          % akkaVersion,

  // --- json ---
  "org.jsoup"         % "jsoup"      % jsoupVersion,
  "com.typesafe.play" %% "play-json" % "2.6.0-M1",

  // -- mysql --
  "com.typesafe.slick" %% "slick"               % "3.2.0",
  "com.typesafe.slick" %% "slick-hikaricp"      % "3.2.0",
  "mysql"              % "mysql-connector-java" % "5.1.40",
  "com.github.tototoshi" %% "slick-joda-mapper" % "2.3.0",

  // -- spark --
  "org.apache.spark" %% "spark-core" % "2.1.0",
  "org.apache.spark" %% "spark-sql" % "2.1.0",
  "com.datastax.spark" %% "spark-cassandra-connector" % "2.0.1",
  //  "io.netty" % "netty-transport-native-epoll" % "4.1.9.Final", // disabled due to java8 bug for spark

  //elastic search
  "org.elasticsearch" % "elasticsearch-spark-20_2.11" % "5.3.1"
)

dependencyOverrides ++= Set(
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.5" //override jackson version for spark libs
)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _ *) => MergeStrategy.discard
  case PathList("reference.conf")     => MergeStrategy.concat
  case x                              => MergeStrategy.first
}
