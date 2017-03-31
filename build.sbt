name := "com.youleligou.crawler"

organization := "com.youleligou"

version := "1.0-SNAPSHOT"

scalaVersion := "2.12.1"

updateOptions := updateOptions.value.withCachedResolution(true)

val akkaVersion = "2.4.17"

val httpClientVersion = "4.4.1"

val commons_ioVersion = "2.4"

val commons_langVersion = "3.4"

val jacksonVersion = "1.9.13"

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
//  web client
  "com.typesafe.play" %% "play-ahc-ws-standalone" % "1.0.0-M6",
//  guice
  "net.codingwell" %% "scala-guice" % "4.1.0",
//  redis
  "com.github.etaty" %% "rediscala" % "1.8.0",
//  akka
  "com.typesafe.akka"         %% "akka-actor"        % akkaVersion,
  "org.apache.httpcomponents" % "httpclient"         % httpClientVersion,
  "org.apache.httpcomponents" % "httpmime"           % httpClientVersion,
  "org.apache.httpcomponents" % "httpcore"           % httpClientVersion,
  "commons-io"                % "commons-io"         % commons_ioVersion,
  "org.apache.commons"        % "commons-lang3"      % commons_langVersion,
  "org.codehaus.jackson"      % "jackson-core-asl"   % jacksonVersion,
  "org.codehaus.jackson"      % "jackson-mapper-asl" % jacksonVersion,
  "org.jsoup"                 % "jsoup"              % jsoupVersion
)
