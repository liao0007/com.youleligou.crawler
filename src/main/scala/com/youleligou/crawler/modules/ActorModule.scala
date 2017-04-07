package com.youleligou.crawler.modules

import javax.inject.Singleton

import akka.actor.SupervisorStrategy.Restart
import akka.actor.{Actor, ActorRef, ActorSystem, OneForOneStrategy, Props}
import akka.contrib.throttle.Throttler.SetTarget
import akka.contrib.throttle.TimerBasedThrottler
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
    case _ => Restart
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
    provideActorPoolRef(system, ProxyAssistantActor, roundRobinPool(1, config.getInt("crawler.actor.proxy-assistant.parallel")))
  }

  @Provides
  @Singleton
  @Named(ProxyAssistantActor.poolThrottlerName)
  def provideProxyAssistantActorPoolThrottlerRef(config: Config,
                                                 system: ActorSystem,
                                                 @Named(ProxyAssistantActor.poolName) proxyAssistantPoolActor: ActorRef): ActorRef = {
    import akka.contrib.throttle.Throttler._
    import scala.concurrent.duration._
    val throttler = system.actorOf(
      Props(
        classOf[TimerBasedThrottler],
        (config.getInt("crawler.actor.proxy-assistant.parallel") - config
          .getInt("crawler.actor.fetch.parallel")) msgsPer config.getInt("crawler.actor.proxy-assistant.timeout").millis
      ))
    throttler ! SetTarget(Some(proxyAssistantPoolActor))
    throttler
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
