package com.youleligou.crawler.module

import javax.inject.Singleton

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.google.inject.{AbstractModule, PrivateModule, Provides}
import com.youleligou.crawler.service._
import com.youleligou.crawler.service.cache.RedisCacheService
import com.youleligou.crawler.service.fetch.HttpClientFetchService
import com.youleligou.crawler.service.filter.DefaultFilterService
import com.youleligou.crawler.service.hash.Md5HashService
import com.youleligou.crawler.service.index.ElasticIndexService
import com.youleligou.crawler.service.parse.html.JsoupParseService
import net.codingwell.scalaguice.{ScalaModule, ScalaPrivateModule}
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
