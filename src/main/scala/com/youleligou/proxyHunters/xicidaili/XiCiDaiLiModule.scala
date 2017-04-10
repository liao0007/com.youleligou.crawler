package com.youleligou.proxyHunters.xicidaili

import javax.inject.Singleton

import akka.actor.SupervisorStrategy.{Escalate, Restart}
import akka.actor.{Actor, ActorRef, ActorSystem, OneForOneStrategy}
import akka.routing.{DefaultResizer, RoundRobinPool}
import com.google.inject.name.{Named, Names}
import com.google.inject.{AbstractModule, Provides}
import com.typesafe.config.Config
import com.youleligou.crawler.modules._
import com.youleligou.crawler.services.ParseService
import com.youleligou.proxyHunters.xicidaili.actors.{ProxyListFetchActor, ProxyListInjectActor, ProxyListParseActor}
import com.youleligou.proxyHunters.xicidaili.services.ProxyListParseService
import net.codingwell.scalaguice.ScalaModule

/**
  * Created by liangliao on 31/3/17.
  */
class XiCiDaiLiModule extends AbstractModule with ScalaModule with GuiceAkkaActorRefProvider {
  /*
  inject actor
   */
  @Provides
  @Named(ProxyListInjectActor.name)
  def provideInjectActorRef(system: ActorSystem): ActorRef = provideActorRef(system, ProxyListInjectActor)

  @Provides
  @Singleton
  @Named(ProxyListInjectActor.poolName)
  def provideInjectActorPoolRef(config: Config, system: ActorSystem): ActorRef = provideActorPoolRef(system, ProxyListInjectActor)

  /*
  fetch actor
   */
  @Provides
  @Named(ProxyListFetchActor.name)
  def provideFetchActorRef(system: ActorSystem): ActorRef = provideActorRef(system, ProxyListFetchActor)

  @Provides
  @Singleton
  @Named(ProxyListFetchActor.poolName)
  def provideFetchActorPoolRef(config: Config, system: ActorSystem): ActorRef = provideActorPoolRef(system, ProxyListFetchActor)

  /*
  parse actor
   */
  @Provides
  @Named(ProxyListParseActor.name)
  def provideParseActorRef(system: ActorSystem): ActorRef = provideActorRef(system, ProxyListParseActor)

  @Provides
  @Singleton
  @Named(ProxyListParseActor.poolName)
  def provideParseActorPoolRef(config: Config, system: ActorSystem): ActorRef = provideActorPoolRef(system, ProxyListParseActor)

  override def configure() {
    bind[Actor].annotatedWith(Names.named(ProxyListInjectActor.name)).to[ProxyListInjectActor]
    bind[Actor].annotatedWith(Names.named(ProxyListFetchActor.name)).to[ProxyListFetchActor]
    bind[Actor].annotatedWith(Names.named(ProxyListParseActor.name)).to[ProxyListParseActor]

    bind[ParseService].annotatedWithName(ProxyListParseService.name).to[ProxyListParseService]
  }
}
