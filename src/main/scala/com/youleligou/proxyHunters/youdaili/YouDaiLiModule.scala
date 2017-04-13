package com.youleligou.proxyHunters.youdaili

import javax.inject.Singleton

import akka.actor.{Actor, ActorRef, ActorSystem}
import com.google.inject.name.{Named, Names}
import com.google.inject.{AbstractModule, Inject, Provider, Provides}
import com.typesafe.config.Config
import com.youleligou.crawler.actors._
import com.youleligou.crawler.modules._
import com.youleligou.crawler.services.{FetchService, HashService, ParseService}
import com.youleligou.proxyHunters.youdaili.services.{ProxyListParseService, ProxyPageParseService}
import net.codingwell.scalaguice.ScalaModule
import redis.RedisClient

/**
  * Created by liangliao on 31/3/17.
  */
class YouDaiLiModule extends AbstractModule with ScalaModule with GuiceAkkaActorRefProvider {
  /*
  actor ref
   */
  @Provides
  @Named(ProxyListInjectActor.name)
  def provideProxyListInjectActorRef(system: ActorSystem): ActorRef = provideActorRef(system, ProxyListInjectActor)

  @Provides
  @Singleton
  @Named(ProxyListInjectActor.poolName)
  def provideProxyListInjectActorPoolRef(config: Config, system: ActorSystem): ActorRef = provideActorPoolRef(system, ProxyListInjectActor)

  @Provides
  @Named(ProxyPageInjectActor.name)
  def provideProxyPageInjectActorRef(system: ActorSystem): ActorRef = provideActorRef(system, ProxyPageInjectActor)

  @Provides
  @Singleton
  @Named(ProxyPageInjectActor.poolName)
  def provideProxyPageInjectActorPoolRef(config: Config, system: ActorSystem): ActorRef = provideActorPoolRef(system, ProxyPageInjectActor)

  override def configure() {
    bind[Actor].annotatedWith(Names.named(ProxyListInjectActor.name)).toProvider(classOf[YouDaiLiModule.ProxyListInjectActorProvider])
    bind[Actor].annotatedWith(Names.named(ProxyListFetchActor.name)).toProvider(classOf[YouDaiLiModule.ProxyListFetchActorProvider])
    bind[Actor].annotatedWith(Names.named(ProxyListParseActor.name)).toProvider(classOf[YouDaiLiModule.ProxyListParseActorProvider])
    bind[ParseService].annotatedWithName(ProxyListParseService.name).to[ProxyListParseService]

    bind[Actor].annotatedWith(Names.named(ProxyPageInjectActor.name)).toProvider(classOf[YouDaiLiModule.ProxyPageInjectActorProvider])
    bind[Actor].annotatedWith(Names.named(ProxyPageFetchActor.name)).toProvider(classOf[YouDaiLiModule.ProxyPageFetchActorProvider])
    bind[Actor].annotatedWith(Names.named(ProxyPageParseActor.name)).toProvider(classOf[YouDaiLiModule.ProxyPageParseActorProvider])
    bind[ParseService].annotatedWithName(ProxyPageParseService.name).to[ProxyPageParseService]
  }
}

object YouDaiLiModule {
  class ProxyListInjectActorProvider @Inject()(config: Config, redisClient: RedisClient, hashService: HashService) extends Provider[Actor] {
    override def get(): Actor = {
      new AbstractInjectActor(config, redisClient, hashService, ProxyListFetchActor) {
        override val CachePrefix: String = ProxyListInjectActor.name
      }
    }
  }

  class ProxyListFetchActorProvider @Inject()(config: Config,
                                              fetchService: FetchService,
                                              @Named(ProxyListInjectActor.poolName) injectorPool: ActorRef)
      extends Provider[Actor] {
    override def get(): Actor = {
      new AbstractFetchActor(config, fetchService, injectorPool, ProxyListParseActor) {}
    }
  }

  class ProxyListParseActorProvider @Inject()(config: Config,
                                              @Named(ProxyListParseService.name) parseService: ParseService,
                                              @Named(IndexActor.poolName) indexerPool: ActorRef,
                                              @Named(ProxyListInjectActor.poolName) injectorPool: ActorRef)
      extends Provider[Actor] {
    override def get(): Actor = {
      new AbstractParseActor(config, parseService, indexerPool, injectorPool) {}
    }
  }

  class ProxyPageInjectActorProvider @Inject()(config: Config, redisClient: RedisClient, hashService: HashService) extends Provider[Actor] {
    override def get(): Actor = {
      new AbstractInjectActor(config, redisClient, hashService, ProxyPageFetchActor) {
        override val CachePrefix: String = ProxyPageInjectActor.name
      }
    }
  }

  class ProxyPageFetchActorProvider @Inject()(config: Config,
                                              fetchService: FetchService,
                                              @Named(ProxyPageInjectActor.poolName) injectorPool: ActorRef)
      extends Provider[Actor] {
    override def get(): Actor = {
      new AbstractFetchActor(config, fetchService, injectorPool, ProxyPageParseActor) {}
    }
  }

  class ProxyPageParseActorProvider @Inject()(config: Config,
                                              @Named(ProxyPageParseService.name) parseService: ParseService,
                                              @Named(IndexActor.poolName) indexerPool: ActorRef,
                                              @Named(ProxyPageInjectActor.poolName) injectorPool: ActorRef)
      extends Provider[Actor] {
    override def get(): Actor = {
      new AbstractParseActor(config, parseService, indexerPool, injectorPool) {}
    }
  }
}
