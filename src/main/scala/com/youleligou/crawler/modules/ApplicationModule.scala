package com.youleligou.crawler.modules

import java.security.MessageDigest
import javax.inject.Inject

import akka.actor.ActorSystem
import com.google.inject.{AbstractModule, Provider}
import com.youleligou.crawler.boot.CrawlerBoot
import com.youleligou.crawler.fetchers.{Fetcher, HttpClientFetcher}
import com.youleligou.crawler.modules.ApplicationModule.{Md5Provider, WsClientProvider}
import net.codingwell.scalaguice.ScalaModule
import play.api.libs.ws.StandaloneWSClient
import play.api.libs.ws.ahc.StandaloneAhcWSClient
import redis.RedisClient

trait Hasher {
  def hash(text: String): String
}

/**
  * Created by liangliao on 31/3/17.
  */
object ApplicationModule {
  class CacheProvider @Inject()(implicit system: ActorSystem) extends Provider[RedisClient] {
    override def get(): RedisClient = RedisClient()
  }
  class WsClientProvider extends Provider[StandaloneWSClient] {
    override def get(): StandaloneWSClient = StandaloneAhcWSClient()
  }
  class Md5Provider extends Provider[Hasher] {
    override def get(): Hasher = new Hasher {
      def hash(text: String): String = MessageDigest.getInstance("MD5").digest(text.getBytes).toString
    }
  }
}

class ApplicationModule extends AbstractModule with ScalaModule {
  override def configure() {
    bind[StandaloneWSClient].toProvider[WsClientProvider].asEagerSingleton()
    bind[RedisClient].toProvider[RedisClient].asEagerSingleton()
    bind[Hasher].toProvider[Md5Provider].asEagerSingleton()

    bind[Fetcher].to[HttpClientFetcher]
  }
}
