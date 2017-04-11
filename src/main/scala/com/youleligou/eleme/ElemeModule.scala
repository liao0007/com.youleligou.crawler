package com.youleligou.eleme

import javax.inject.Singleton

import akka.actor.{Actor, ActorRef, ActorSystem}
import com.google.inject.name.{Named, Names}
import com.google.inject.{AbstractModule, Provides}
import com.typesafe.config.Config
import com.youleligou.crawler.modules._
import com.youleligou.crawler.services.ParseService
import com.youleligou.eleme.actors.{RestaurantFetchActor, RestaurantInjectActor, RestaurantParseActor}
import com.youleligou.eleme.services.RestaurantParseService
import net.codingwell.scalaguice.ScalaModule

/**
  * Created by liangliao on 31/3/17.
  */
class ElemeModule extends AbstractModule with ScalaModule with GuiceAkkaActorRefProvider {
  /*
  inject actor
   */
  @Provides
  @Named(RestaurantInjectActor.name)
  def provideInjectActorRef(system: ActorSystem): ActorRef = provideActorRef(system, RestaurantInjectActor)

  @Provides
  @Singleton
  @Named(RestaurantInjectActor.poolName)
  def provideInjectActorPoolRef(config: Config, system: ActorSystem): ActorRef = provideActorPoolRef(system, RestaurantInjectActor)


  override def configure() {
    bind[Actor].annotatedWith(Names.named(RestaurantInjectActor.name)).to[RestaurantInjectActor]
    bind[Actor].annotatedWith(Names.named(RestaurantFetchActor.name)).to[RestaurantFetchActor]
    bind[Actor].annotatedWith(Names.named(RestaurantParseActor.name)).to[RestaurantParseActor]

    bind[ParseService].annotatedWithName(RestaurantParseService.name).to[RestaurantParseService]
  }

}
