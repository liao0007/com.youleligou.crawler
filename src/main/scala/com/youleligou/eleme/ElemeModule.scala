package com.youleligou.eleme

import javax.inject.Singleton

import akka.actor.SupervisorStrategy.Restart
import akka.actor.{Actor, ActorRef, ActorSystem, OneForOneStrategy}
import akka.routing.{DefaultResizer, RoundRobinPool}
import com.google.inject.name.{Named, Names}
import com.google.inject.{AbstractModule, Provides}
import com.typesafe.config.Config
import com.youleligou.crawler.modules._
import com.youleligou.crawler.services.{InjectService, ParseService}
import com.youleligou.eleme.actors.{RestaurantFetchActor, RestaurantInjectActor, RestaurantParseActor}
import com.youleligou.eleme.services.{RestaurantInjectService, RestaurantParseService}
import net.codingwell.scalaguice.ScalaModule

/**
  * Created by liangliao on 31/3/17.
  */
class ElemeModule extends AbstractModule with ScalaModule with GuiceAkkaActorRefProvider {

  private val restartSupervisorStrategy: OneForOneStrategy = OneForOneStrategy() {
    case _ => Restart
  }

  private def roundRobinPool(lowerBound: Int, upperBound: Int): RoundRobinPool =
    RoundRobinPool(nrOfInstances = lowerBound,
      resizer = Some(DefaultResizer(lowerBound, upperBound)),
      supervisorStrategy = restartSupervisorStrategy)

  /*
  inject actor
   */
  @Provides
  @Named(RestaurantInjectActor.name)
  def provideInjectActorRef(system: ActorSystem): ActorRef = provideActorRef(system, RestaurantInjectActor)

  @Provides
  @Singleton
  @Named(RestaurantInjectActor.poolName)
  def provideInjectActorPoolRef(config: Config, system: ActorSystem): ActorRef = {
    provideActorPoolRef(system, RestaurantInjectActor, roundRobinPool(1, config.getInt("crawler.actor.inject.parallel")))
  }

  /*
  fetch actor
   */
  @Provides
  @Named(RestaurantFetchActor.name)
  def provideFetchActorRef(system: ActorSystem): ActorRef = provideActorRef(system, RestaurantFetchActor)

  @Provides
  @Singleton
  @Named(RestaurantFetchActor.poolName)
  def provideFetchActorPoolRef(config: Config, system: ActorSystem): ActorRef = {
    provideActorPoolRef(system, RestaurantFetchActor, roundRobinPool(1, config.getInt("crawler.actor.fetch.parallel")))
  }

  /*
  parse actor
   */
  @Provides
  @Named(RestaurantParseActor.name)
  def provideParseActorRef(system: ActorSystem): ActorRef = provideActorRef(system, RestaurantParseActor)

  @Provides
  @Singleton
  @Named(RestaurantParseActor.poolName)
  def provideParseActorPoolRef(config: Config, system: ActorSystem): ActorRef = {
    provideActorPoolRef(system, RestaurantParseActor, roundRobinPool(1, config.getInt("crawler.actor.parse.parallel")))
  }

  override def configure() {
    install(new ConfigModule)
    install(new AkkaModule)
    install(new ServiceModule)
    install(new ActorModule)

    bind[Actor].annotatedWith(Names.named(RestaurantInjectActor.name)).to[RestaurantInjectActor]
    bind[Actor].annotatedWith(Names.named(RestaurantFetchActor.name)).to[RestaurantFetchActor]
    bind[Actor].annotatedWith(Names.named(RestaurantParseActor.name)).to[RestaurantParseActor]

    bind[ParseService].annotatedWithName(RestaurantParseService.name).to[RestaurantParseService]
    bind[InjectService].annotatedWithName(RestaurantInjectService.name).to[RestaurantInjectService]
  }

}
