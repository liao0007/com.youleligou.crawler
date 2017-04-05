package com.youleligou.crawler.modules

import javax.inject.Singleton

import akka.actor.SupervisorStrategy.Escalate
import akka.actor.{Actor, ActorRef, ActorSystem, OneForOneStrategy}
import akka.routing.{DefaultResizer, RoundRobinPool}
import com.google.inject.name.{Named, Names}
import com.google.inject.{AbstractModule, Provides}
import com.typesafe.config.Config
import com.youleligou.crawler.actors._
import net.codingwell.scalaguice.ScalaModule

/**
  * Created by liangliao on 1/4/17.
  */
class ActorModule extends AbstractModule with ScalaModule with GuiceAkkaActorRefProvider {

  private val restartSupervisorStrategy: OneForOneStrategy = OneForOneStrategy() {
    case _ => Escalate
  }

  private def roundRobinPool(lowerBound: Int, upperBound: Int): RoundRobinPool =
    RoundRobinPool(nrOfInstances = lowerBound,
      resizer = Some(DefaultResizer(lowerBound, upperBound)),
      supervisorStrategy = restartSupervisorStrategy)

  /*
  count actor
   */
  @Provides
  @Named(CountActor.name)
  def provideAuditActorRef(system: ActorSystem): ActorRef = provideActorRef(system, CountActor)

  @Provides
  @Singleton
  @Named(CountActor.poolName)
  def provideCountActorPoolRef(config: Config, system: ActorSystem): ActorRef = {
    provideActorPoolRef(system, CountActor, roundRobinPool(1, config.getInt("crawler.actor.count.parallel")))
  }

  /*
  proxy assistant actor
   */
  @Provides
  @Named(ProxyAssistantActor.name)
  def provideProxyAssistantActorRef(system: ActorSystem): ActorRef = provideActorRef(system, ProxyAssistantActor)

  @Provides
  @Singleton
  @Named(ProxyAssistantActor.poolName)
  def provideProxyAssistantActorPoolRef(config: Config, system: ActorSystem): ActorRef = {
    provideActorPoolRef(system, ProxyAssistantActor, roundRobinPool(1, config.getInt("crawler.actor.proxy.parallel")))
  }

  /*
  index actor
   */
  @Provides
  @Named(IndexActor.name)
  def provideIndexActorRef(system: ActorSystem): ActorRef = provideActorRef(system, IndexActor)

  @Provides
  @Singleton
  @Named(IndexActor.poolName)
  def provideIndexActorPoolRef(config: Config, system: ActorSystem): ActorRef = {
    provideActorPoolRef(system, IndexActor, roundRobinPool(1, config.getInt("crawler.actor.index.parallel")))
  }

  override def configure() {
    bind[Actor].annotatedWith(Names.named(CountActor.name)).to[CountActor]
    bind[Actor].annotatedWith(Names.named(ProxyAssistantActor.name)).to[ProxyAssistantActor]
    bind[Actor].annotatedWith(Names.named(IndexActor.name)).to[IndexActor]
  }
}
