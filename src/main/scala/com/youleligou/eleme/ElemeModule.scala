package com.youleligou.eleme

import javax.inject.Singleton

import akka.actor.{Actor, ActorRef, ActorSystem}
import com.google.inject.name.{Named, Names}
import com.google.inject.{AbstractModule, Inject, Provider, Provides}
import com.typesafe.config.Config
import com.youleligou.crawler.actors._
import com.youleligou.crawler.modules._
import com.youleligou.crawler.services.{FetchService, HashService, ParseService}
import com.youleligou.eleme.services.RestaurantParseService
import net.codingwell.scalaguice.ScalaModule
import redis.RedisClient

/**
  * Created by liangliao on 31/3/17.
  */
class ElemeModule extends AbstractModule with ScalaModule with GuiceAkkaActorRefProvider {
  /*
  bind actor refs
   */
  @Provides
  @Named(RestaurantInjectActor.name)
  def injectActorRef(system: ActorSystem): ActorRef = provideActorRef(system, RestaurantInjectActor)

  @Provides
  @Singleton
  @Named(RestaurantInjectActor.poolName)
  def injectActorPoolRef(config: Config, system: ActorSystem): ActorRef = provideActorPoolRef(system, RestaurantInjectActor)

  //bind actors and services
  override def configure() {
    bind[Actor].annotatedWith(Names.named(RestaurantInjectActor.name)).toProvider(classOf[ElemeModule.RestaurantInjectActorProvider])
    bind[Actor].annotatedWith(Names.named(RestaurantFetchActor.name)).toProvider[ElemeModule.RestaurantFetchActorProvider]
    bind[Actor].annotatedWith(Names.named(RestaurantParseActor.name)).toProvider[ElemeModule.RestaurantParseActorProvider]

    bind[ParseService].annotatedWithName(RestaurantParseService.name).to[RestaurantParseService]
  }
}

object ElemeModule {
  class RestaurantInjectActorProvider @Inject()(config: Config, redisClient: RedisClient, hashService: HashService) extends Provider[Actor] {
    override def get(): Actor = {
      new AbstractInjectActor(config, redisClient, hashService, RestaurantFetchActor) {
        override val Prefix: String = "ElemeRestaurant"
      }
    }
  }

  class RestaurantFetchActorProvider @Inject()(config: Config,
                                               fetchService: FetchService,
                                               @Named(RestaurantInjectActor.poolName) injectorPool: ActorRef,
                                               @Named(ProxyAssistantActor.poolName) proxyAssistantPool: ActorRef)
      extends Provider[Actor] {
    override def get(): Actor = {
      new AbstractFetchActor(config, fetchService, injectorPool, proxyAssistantPool, RestaurantParseActor) {}
    }
  }

  class RestaurantParseActorProvider @Inject()(config: Config,
                                               @Named(RestaurantParseService.name) parseService: ParseService,
                                               @Named(IndexActor.poolName) indexerPool: ActorRef,
                                               @Named(RestaurantInjectActor.poolName) injectorPool: ActorRef)
      extends Provider[Actor] {
    override def get(): Actor = {
      new AbstractParseActor(config, parseService, indexerPool, injectorPool) {}
    }
  }
}
