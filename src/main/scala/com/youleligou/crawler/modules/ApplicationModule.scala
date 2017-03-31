package com.youleligou.crawler.modules

import java.security.MessageDigest

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.google.inject.{AbstractModule, Provider, Provides}
import com.youleligou.crawler.fetchers.{Fetcher, HttpClientFetcher}
import com.youleligou.crawler.indexers.{ElasticIndexer, Indexer}
import com.youleligou.crawler.modules.ApplicationModule.Md5Provider
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

class ApplicationModule extends AbstractModule with ScalaModule {
  @Provides
  def provideRedisClient(implicit system: ActorSystem): RedisClient = {
    RedisClient()
  }

  @Provides
  def provideStandaloneAhcWSClient(implicit system: ActorSystem) = {
    implicit val materializer = ActorMaterializer()
    StandaloneAhcWSClient()
  }

  override def configure() {
    bind[Hasher].toProvider[Md5Provider].asEagerSingleton()
    bind[Fetcher].to[HttpClientFetcher]
    bind[Indexer].to[ElasticIndexer]
  }
}
