package com.youleligou.crawler.modules

import java.security.MessageDigest

import akka.actor.{Actor, ActorRef, ActorSystem}
import akka.stream.ActorMaterializer
import com.google.inject.name.{Named, Names}
import com.google.inject.{AbstractModule, Provider, Provides}
import com.youleligou.crawler.actors.{CountActor, FetchActor, IndexActor, ParseActor}
import com.youleligou.crawler.fetchers.{Fetcher, HttpClientFetcher}
import com.youleligou.crawler.indexers.{ElasticIndexer, Indexer}
import com.youleligou.crawler.modules.ApplicationModule.Md5Provider
import com.youleligou.crawler.parsers.{JsoupParser, Parser}
import net.codingwell.scalaguice.ScalaModule
import play.api.libs.ws.ahc.StandaloneAhcWSClient
import redis.RedisClient

trait Hasher {
  def hash(text: String): String
}

/**
  * Created by liangliao on 31/3/17.
  */
object ApplicationModule {

  class Md5Provider extends Provider[Hasher] {
    override def get(): Hasher = new Hasher {
      def hash(text: String): String = MessageDigest.getInstance("MD5").digest(text.getBytes).toString
    }
  }

}

class ApplicationModule extends AbstractModule with ScalaModule with GuiceAkkaActorRefProvider {
  @Provides
  def provideRedisClient(implicit system: ActorSystem): RedisClient = {
    RedisClient()
  }

  @Provides
  def provideStandaloneAhcWSClient(implicit system: ActorSystem) = {
    implicit val materializer = ActorMaterializer()
    StandaloneAhcWSClient()
  }

  /**
    * provide Actors
    */
  @Provides
  @Named(CountActor.name)
  def provideAuditCompanionRef(system: ActorSystem): ActorRef = provideActorRef(system, CountActor.name)

  @Provides
  @Named(FetchActor.name)
  def provideFetchActorRef(system: ActorSystem): ActorRef = provideActorRef(system, FetchActor.name)

  @Provides
  @Named(ParseActor.name)
  def provideParseActorRef(system: ActorSystem): ActorRef = provideActorRef(system, ParseActor.name)

  @Provides
  @Named(IndexActor.name)
  def provideIndexActorRef(system: ActorSystem): ActorRef = provideActorRef(system, IndexActor.name)

  override def configure() {
    bind[Hasher].toProvider[Md5Provider].asEagerSingleton()
    bind[Fetcher].to[HttpClientFetcher]
    bind[Indexer].to[ElasticIndexer]
    bind[Parser].to[JsoupParser]

    bind[Actor].annotatedWith(Names.named(CountActor.name)).to[CountActor]
    bind[Actor].annotatedWith(Names.named(FetchActor.name)).to[FetchActor]
    bind[Actor].annotatedWith(Names.named(ParseActor.name)).to[ParseActor]
    bind[Actor].annotatedWith(Names.named(IndexActor.name)).to[IndexActor]
  }
}
