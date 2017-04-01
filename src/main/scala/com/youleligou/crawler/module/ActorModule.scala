package com.youleligou.crawler.module

import javax.inject.Singleton

import akka.actor.{Actor, ActorRef, ActorSystem}
import akka.routing.BalancingPool
import com.google.inject.{AbstractModule, Provides}
import com.google.inject.name.{Named, Names}
import com.typesafe.config.Config
import com.youleligou.crawler.actor._
import net.codingwell.scalaguice.ScalaModule

/**
  * Created by liangliao on 1/4/17.
  */
class ActorModule extends AbstractModule with ScalaModule with GuiceAkkaActorRefProvider {

  /**
    * provide Actors
    */
  @Provides
  @Named(InjectActor.name)
  def provideInjectActorRef(system: ActorSystem): ActorRef = provideActorRef(system, InjectActor)

  @Provides
  @Singleton
  @Named(InjectActor.poolName)
  def provideInjectActorPoolRef(config: Config, system: ActorSystem): ActorRef = {
    provideActorPoolRef(system, InjectActor, BalancingPool(config.getInt("crawler.actor.inject.parallel")))
  }

  @Provides
  @Named(CountActor.name)
  def provideAuditActorRef(system: ActorSystem): ActorRef = provideActorRef(system, CountActor)

  @Provides
  @Singleton
  @Named(CountActor.poolName)
  def provideCountActorPoolRef(config: Config, system: ActorSystem): ActorRef = {
    provideActorPoolRef(system, CountActor, BalancingPool(config.getInt("crawler.actor.count.parallel")))
  }

  @Provides
  @Named(FetchActor.name)
  def provideFetchActorRef(system: ActorSystem): ActorRef = provideActorRef(system, FetchActor)

  @Provides
  @Singleton
  @Named(FetchActor.poolName)
  def provideFetchActorPoolRef(config: Config, system: ActorSystem): ActorRef = {
    provideActorPoolRef(system, FetchActor, BalancingPool(config.getInt("crawler.actor.fetch.parallel")))
  }

  @Provides
  @Named(ParseActor.name)
  def provideParseActorRef(system: ActorSystem): ActorRef = provideActorRef(system, ParseActor)

  @Provides
  @Singleton
  @Named(ParseActor.poolName)
  def provideParseActorPoolRef(config: Config, system: ActorSystem): ActorRef = {
    provideActorPoolRef(system, ParseActor, BalancingPool(config.getInt("crawler.actor.parse.parallel")))
  }

  @Provides
  @Named(IndexActor.name)
  def provideIndexActorRef(system: ActorSystem): ActorRef = provideActorRef(system, IndexActor)

  @Provides
  @Singleton
  @Named(IndexActor.poolName)
  def provideIndexActorPoolRef(config: Config, system: ActorSystem): ActorRef = {
    provideActorPoolRef(system, IndexActor, BalancingPool(config.getInt("crawler.actor.index.parallel")))
  }

  override def configure() {
    bind[Actor].annotatedWith(Names.named(InjectActor.name)).to[InjectActor]
    bind[Actor].annotatedWith(Names.named(CountActor.name)).to[CountActor]
    bind[Actor].annotatedWith(Names.named(FetchActor.name)).to[FetchActor]
    bind[Actor].annotatedWith(Names.named(ParseActor.name)).to[ParseActor]
    bind[Actor].annotatedWith(Names.named(IndexActor.name)).to[IndexActor]
  }
}
