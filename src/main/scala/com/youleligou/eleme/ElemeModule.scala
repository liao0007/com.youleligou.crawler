package com.youleligou.eleme

import javax.inject.Singleton

import akka.actor.{Actor, ActorRef, ActorSystem}
import com.google.inject.name.{Named, Names}
import com.google.inject.{AbstractModule, Inject, Provider, Provides}
import com.typesafe.config.Config
import com.youleligou.crawler.actors._
import com.youleligou.crawler.modules._
import com.youleligou.crawler.services.{FetchService, HashService, ParseService}
import com.youleligou.eleme.services.{FoodParseService, RestaurantParseService}
import net.codingwell.scalaguice.ScalaModule
import redis.RedisClient

/**
  * Created by liangliao on 31/3/17.
  */
class ElemeModule extends AbstractModule with ScalaModule with GuiceAkkaActorRefProvider {
  /*
  bind actor refs
   */
//  restaurant
  @Provides
  @Named(RestaurantInjectActor.name)
  def restaurantInjectActorRef(system: ActorSystem): ActorRef = provideActorRef(system, RestaurantInjectActor)

  @Provides
  @Singleton
  @Named(RestaurantInjectActor.poolName)
  def restaurantInjectActorPoolRef(config: Config, system: ActorSystem): ActorRef = provideActorPoolRef(system, RestaurantInjectActor)

//  food
  @Provides
  @Named(FoodInjectActor.name)
  def foodInjectActorRef(system: ActorSystem): ActorRef = provideActorRef(system, FoodInjectActor)

  @Provides
  @Singleton
  @Named(FoodInjectActor.poolName)
  def foodInjectActorPoolRef(config: Config, system: ActorSystem): ActorRef = provideActorPoolRef(system, FoodInjectActor)

  //bind actors and services
  override def configure() {
    bind[Actor].annotatedWith(Names.named(RestaurantInjectActor.name)).toProvider(classOf[ElemeModule.RestaurantInjectActorProvider])
    bind[Actor].annotatedWith(Names.named(RestaurantFetchActor.name)).toProvider[ElemeModule.RestaurantFetchActorProvider]
    bind[Actor].annotatedWith(Names.named(RestaurantParseActor.name)).toProvider[ElemeModule.RestaurantParseActorProvider]
    bind[ParseService].annotatedWithName(RestaurantParseService.name).to[RestaurantParseService]

    bind[Actor].annotatedWith(Names.named(FoodInjectActor.name)).toProvider(classOf[ElemeModule.FoodInjectActorProvider])
    bind[Actor].annotatedWith(Names.named(FoodFetchActor.name)).toProvider[ElemeModule.FoodFetchActorProvider]
    bind[Actor].annotatedWith(Names.named(FoodParseActor.name)).toProvider[ElemeModule.FoodParseActorProvider]
    bind[ParseService].annotatedWithName(FoodParseService.name).to[FoodParseService]
  }
}

object ElemeModule {
//  restaurant
  class RestaurantInjectActorProvider @Inject()(config: Config, redisClient: RedisClient, hashService: HashService) extends Provider[Actor] {
    override def get(): Actor = {
      new AbstractInjectActor(config, redisClient, hashService, RestaurantFetchActor) {
        override val CachePrefix: String = RestaurantInjectActor.name
      }
    }
  }

  class RestaurantFetchActorProvider @Inject()(config: Config,
                                               fetchService: FetchService,
                                               @Named(RestaurantInjectActor.poolName) injectorPool: ActorRef)
      extends Provider[Actor] {
    override def get(): Actor = {
      new AbstractFetchActor(config, fetchService, injectorPool, RestaurantParseActor) {}
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

//  food
  class FoodInjectActorProvider @Inject()(config: Config, redisClient: RedisClient, hashService: HashService) extends Provider[Actor] {
    override def get(): Actor = {
      new AbstractInjectActor(config, redisClient, hashService, FoodFetchActor) {
        override val CachePrefix: String = FoodInjectActor.name
      }
    }
  }

  class FoodFetchActorProvider @Inject()(config: Config,
                                         fetchService: FetchService,
                                         @Named(FoodInjectActor.poolName) injectorPool: ActorRef)
      extends Provider[Actor] {
    override def get(): Actor = {
      new AbstractFetchActor(config, fetchService, injectorPool, FoodParseActor) {}
    }
  }

  class FoodParseActorProvider @Inject()(config: Config,
                                         @Named(FoodParseService.name) parseService: ParseService,
                                         @Named(IndexActor.poolName) indexerPool: ActorRef,
                                         @Named(FoodInjectActor.poolName) injectorPool: ActorRef)
      extends Provider[Actor] {
    override def get(): Actor = {
      new AbstractParseActor(config, parseService, indexerPool, injectorPool) {}
    }
  }
}
