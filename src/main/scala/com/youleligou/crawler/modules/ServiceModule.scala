package com.youleligou.crawler.modules

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.google.inject.{AbstractModule, Provides}
import com.typesafe.config.Config
import com.youleligou.crawler.services._
import com.youleligou.crawler.services.cache.RedisCacheService
import com.youleligou.crawler.services.fetch.HttpClientFetchService
import com.youleligou.crawler.services.filter.DefaultFilterService
import com.youleligou.crawler.services.hash.Md5HashService
import com.youleligou.crawler.services.index.ElasticIndexService
import net.codingwell.scalaguice.ScalaModule
import play.api.libs.ws.ahc.StandaloneAhcWSClient
import redis.RedisClient

/**
  * Created by liangliao on 31/3/17.
  */
class ServiceModule extends AbstractModule with ScalaModule {

  @Provides
  def provideStandaloneAhcWSClient()(implicit system: ActorSystem): StandaloneAhcWSClient = {
    implicit val materializer = ActorMaterializer()
    StandaloneAhcWSClient()
  }

  @Provides
  def provideRedisClient()(config: Config)(implicit system: ActorSystem): RedisClient = {
    val redisConfig: Config = config.getConfig("cache.redis")
    RedisClient(host = redisConfig.getString("host"), port = redisConfig.getInt("port"), password = Some(redisConfig.getString("password")))
  }

  override def configure() {
    bind[HashService].to[Md5HashService]
    bind[FetchService].to[HttpClientFetchService]
    bind[IndexService].to[ElasticIndexService]
    bind[CacheService].to[RedisCacheService]
    bind[FilterService].to[DefaultFilterService]
    bind[ProxyAssistantService].to[DefaultProxyAssistantService]
  }
}