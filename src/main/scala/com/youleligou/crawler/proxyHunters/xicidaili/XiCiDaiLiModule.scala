package com.youleligou.crawler.proxyHunters.xicidaili

import javax.inject.Singleton

import akka.actor.SupervisorStrategy.Restart
import akka.actor.{Actor, ActorRef, ActorSystem, OneForOneStrategy}
import akka.routing.{DefaultResizer, RoundRobinPool}
import com.google.inject.name.{Named, Names}
import com.google.inject.{AbstractModule, Provides}
import com.typesafe.config.Config
import com.youleligou.crawler.modules.GuiceAkkaActorRefProvider
import com.youleligou.crawler.proxyHunters.xicidaili.actors.{ProxyListInjectActor, ProxyListParseActor}
import com.youleligou.crawler.proxyHunters.xicidaili.services.{ProxyListInjectService, ProxyListParseService}
import com.youleligou.crawler.services.{InjectService, ParseService}
import net.codingwell.scalaguice.ScalaModule

/**
  * Created by liangliao on 31/3/17.
  */
class XiCiDaiLiModule extends AbstractModule with ScalaModule with GuiceAkkaActorRefProvider {

  private val restartSupervisorStrategy: OneForOneStrategy = OneForOneStrategy() {
    case _ => Restart
  }

  private def roundRobinPool(lowerBound: Int, upperBound: Int): RoundRobinPool =
    RoundRobinPool(nrOfInstances = lowerBound,
      resizer = Some(DefaultResizer(lowerBound, upperBound)),
      supervisorStrategy = restartSupervisorStrategy)

  /**
    * provide Actors
    */
  @Provides
  @Named(ProxyListInjectActor.name)
  def provideInjectActorRef(system: ActorSystem): ActorRef = provideActorRef(system, ProxyListInjectActor)

  @Provides
  @Singleton
  @Named(ProxyListInjectActor.poolName)
  def provideInjectActorPoolRef(config: Config, system: ActorSystem): ActorRef = {
    provideActorPoolRef(system, ProxyListInjectActor, roundRobinPool(1, config.getInt("crawler.actor.inject.parallel")))
  }

  @Provides
  @Named(ProxyListParseActor.name)
  def provideParseActorRef(system: ActorSystem): ActorRef = provideActorRef(system, ProxyListParseActor)

  @Provides
  @Singleton
  @Named(ProxyListParseActor.poolName)
  def provideParseActorPoolRef(config: Config, system: ActorSystem): ActorRef = {
    provideActorPoolRef(system, ProxyListParseActor, roundRobinPool(1, config.getInt("crawler.actor.parse.parallel")))
  }

  override def configure() {
    bind[Actor].annotatedWith(Names.named(ProxyListInjectActor.name)).to[ProxyListInjectActor]
    bind[Actor].annotatedWith(Names.named(ProxyListParseActor.name)).to[ProxyListParseActor]

    bind[ParseService].annotatedWithName(ProxyListParseService.name).to[ProxyListParseService]
    bind[InjectService].annotatedWithName(ProxyListInjectService.name).to[ProxyListInjectService]
  }
}
