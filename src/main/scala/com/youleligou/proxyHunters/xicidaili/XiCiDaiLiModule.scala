package com.youleligou.proxyHunters.xicidaili

import javax.inject.Singleton

import akka.actor.{Actor, ActorRef, ActorSystem}
import com.google.inject.name.{Named, Names}
import com.google.inject.{AbstractModule, Inject, Provider, Provides}
import com.typesafe.config.Config
import com.youleligou.crawler.actors._
import com.youleligou.crawler.modules._
import com.youleligou.crawler.services.{FetchService, HashService, ParseService}
import com.youleligou.eleme.RestaurantInjectActor
import com.youleligou.proxyHunters.xicidaili.services.ProxyListParseService
import net.codingwell.scalaguice.ScalaModule
import redis.RedisClient

/**
  * Created by liangliao on 31/3/17.
  */
class XiCiDaiLiModule extends AbstractModule with ScalaModule with GuiceAkkaActorRefProvider {
  /*
  bind actor ref
   */
  @Provides
  @Named(ProxyListInjectActor.name)
  def provideInjectActorRef(system: ActorSystem): ActorRef = provideActorRef(system, ProxyListInjectActor)

  @Provides
  @Singleton
  @Named(ProxyListInjectActor.poolName)
  def provideInjectActorPoolRef(config: Config, system: ActorSystem): ActorRef = provideActorPoolRef(system, ProxyListInjectActor)

  //bind actor and service
  override def configure() {
    bind[Actor].annotatedWith(Names.named(ProxyListInjectActor.name)).toProvider(classOf[XiCiDaiLiModule.ProxyListInjectActorProvider])
    bind[Actor].annotatedWith(Names.named(ProxyListFetchActor.name)).toProvider(classOf[XiCiDaiLiModule.ProxyListFetchActorProvider])
    bind[Actor].annotatedWith(Names.named(ProxyListParseActor.name)).toProvider(classOf[XiCiDaiLiModule.ProxyListParseActorProvider])

    bind[ParseService].annotatedWithName(ProxyListParseService.name).to[ProxyListParseService]
  }
}

object XiCiDaiLiModule {
  class ProxyListInjectActorProvider @Inject()(config: Config, redisClient: RedisClient, hashService: HashService) extends Provider[Actor] {
    override def get(): Actor = {
      new AbstractInjectActor(config, redisClient, hashService, ProxyListFetchActor) {
        override val CachePrefix: String = ProxyListInjectActor.name
      }
    }
  }

  class ProxyListFetchActorProvider @Inject()(config: Config,
                                              fetchService: FetchService,
                                              @Named(ProxyListInjectActor.poolName) injectorPool: ActorRef,
                                              @Named(ProxyAssistantActor.poolName) proxyAssistantPool: ActorRef)
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
}
