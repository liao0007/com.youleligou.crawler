package com.youleligou.crawler.module

import javax.inject.Singleton

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.google.inject.{AbstractModule, Provides}
import com.youleligou.crawler.service.cache.{CacheService, RedisCacheService}
import com.youleligou.crawler.service.fetch.{FetchService, HttpClientFetchService}
import com.youleligou.crawler.service.hash.{HashService, Md5HashService}
import com.youleligou.crawler.service.index.{ElasticIndexService, IndexService}
import com.youleligou.crawler.service.parse.{JsoupParseService, ParseService}
import net.codingwell.scalaguice.ScalaModule
import play.api.libs.ws.ahc.StandaloneAhcWSClient

/**
  * Created by liangliao on 31/3/17.
  */
class ServiceModule extends AbstractModule with ScalaModule with GuiceAkkaActorRefProvider {

  @Provides
  @Singleton
  def provideStandaloneAhcWSClient(implicit system: ActorSystem) = {
    implicit val materializer = ActorMaterializer()
    StandaloneAhcWSClient()
  }

  override def configure() {
    bind[HashService].to[Md5HashService].asEagerSingleton()
    bind[FetchService].to[HttpClientFetchService].asEagerSingleton()
    bind[IndexService].to[ElasticIndexService].asEagerSingleton()
    bind[ParseService].to[JsoupParseService].asEagerSingleton()
    bind[CacheService].to[RedisCacheService].asEagerSingleton()
  }
}
