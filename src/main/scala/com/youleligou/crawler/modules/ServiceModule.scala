package com.youleligou.crawler.modules

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.google.inject.{AbstractModule, Provides}
import com.youleligou.crawler.services._
import com.youleligou.crawler.services.cache.RedisCacheService
import com.youleligou.crawler.services.fetch.HttpClientFetchService
import com.youleligou.crawler.services.filter.DefaultFilterService
import com.youleligou.crawler.services.hash.Md5HashService
import com.youleligou.crawler.services.index.ElasticIndexService
import net.codingwell.scalaguice.ScalaModule
import play.api.libs.ws.ahc.StandaloneAhcWSClient

/**
  * Created by liangliao on 31/3/17.
  */
class ServiceModule extends AbstractModule with ScalaModule {

  @Provides
  def provideStandaloneAhcWSClient()(implicit system: ActorSystem): StandaloneAhcWSClient = {
    implicit val materializer = ActorMaterializer()
    StandaloneAhcWSClient()
  }

  override def configure() {
    bind[HashService].to[Md5HashService].asEagerSingleton()
    bind[FetchService].to[HttpClientFetchService].asEagerSingleton()
    bind[IndexService].to[ElasticIndexService].asEagerSingleton()
    bind[CacheService].to[RedisCacheService].asEagerSingleton()
    bind[FilterService].to[DefaultFilterService].asEagerSingleton()
  }
}
