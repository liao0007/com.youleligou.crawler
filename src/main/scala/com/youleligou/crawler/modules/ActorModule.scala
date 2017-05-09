package com.youleligou.crawler.modules

import javax.inject.Singleton

import akka.actor.{Actor, ActorRef, ActorSystem}
import com.google.inject.name.{Named, Names}
import com.google.inject.{AbstractModule, Provides}
import com.typesafe.config.Config
import com.youleligou.core.modules.GuiceAkkaActorRefProvider
import com.youleligou.crawler.actors._
import net.codingwell.scalaguice.ScalaModule

/**
  * Created by liangliao on 1/4/17.
  */
class ActorModule extends AbstractModule with ScalaModule with GuiceAkkaActorRefProvider {
  /*
  count actor
   */
  @Provides
  @Named(Counter.Name)
  def provideCounter(system: ActorSystem): ActorRef = provideActorRef(system, Counter)

  /*
  proxy assistant actor
   */
  @Provides
  @Named(ProxyAssistant.Name)
  def provideProxyAssistant(system: ActorSystem): ActorRef = provideActorRef(system, ProxyAssistant)

  /*
  inject actor
   */
  @Provides
  @Named(Injector.Name)
  def provideInjector(system: ActorSystem): ActorRef = provideActorRef(system, Injector)

  @Provides
  @Singleton
  @Named(Injector.PoolName)
  def provideInjectActors(config: Config, system: ActorSystem): ActorRef = provideActorPoolRef(system, Injector)

  @Provides
  @Named(Fetcher.Name)
  def provideFetcher(system: ActorSystem): ActorRef = provideActorRef(system, Fetcher)

  @Provides
  @Named(Parser.Name)
  def provideParser(system: ActorSystem): ActorRef = provideActorRef(system, Parser)

  @Provides
  @Named(Indexer.Name)
  def provideIndexer(system: ActorSystem): ActorRef = provideActorRef(system, Indexer)

  override def configure() {
    bind[Actor].annotatedWith(Names.named(Counter.Name)).to[Counter]
    bind[Actor].annotatedWith(Names.named(Indexer.Name)).to[Indexer]
    bind[Actor].annotatedWith(Names.named(ProxyAssistant.Name)).to[ProxyAssistant]
    bind[Actor].annotatedWith(Names.named(Injector.Name)).to[Injector]
    bind[Actor].annotatedWith(Names.named(Fetcher.Name)).to[Fetcher]
    bind[Actor].annotatedWith(Names.named(Parser.Name)).to[Parser]
  }
}
