package com.youleligou.proxyHunters.xicidaili

import javax.inject.Singleton

import akka.actor.{Actor, ActorRef, ActorSystem}
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

  override def configure() {
    bind[Actor].annotatedWith(Names.named(ProxyListInjectActor.name)).to[ProxyListInjectActor]
    bind[Actor].annotatedWith(Names.named(ProxyListFetchActor.name)).to[ProxyListFetchActor]
    bind[Actor].annotatedWith(Names.named(ProxyListParseActor.name)).to[ProxyListParseActor]

    bind[ParseService].annotatedWithName(ProxyListParseService.name).to[ProxyListParseService]
  }
}
