package com.youleligou

import akka.actor.{ActorRef, ActorSystem}
import com.google.inject.name.Named
import com.google.inject.{Guice, Inject}
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.actors.ProxyAssistantActor
import com.youleligou.crawler.actors.ProxyAssistantActor.{CheckCache, CleanUp}
import com.youleligou.crawler.modules.{ActorModule, AkkaModule, ConfigModule, ServiceModule}
import com.youleligou.eleme.ElemeModule
import com.youleligou.proxyHunters.xicidaili.XiCiDaiLiModule

import scala.concurrent.duration.{FiniteDuration, MILLISECONDS, SECONDS}

/**
  * Created by liangliao on 31/3/17.
  */
class ProxyAssistantBootstrap @Inject()(config: Config, system: ActorSystem, @Named(ProxyAssistantActor.poolName) proxyAssistantActor: ActorRef)
  extends LazyLogging {

  import system.dispatcher

  def start(): Unit = {
    system.scheduler.scheduleOnce(FiniteDuration(0, SECONDS), proxyAssistantActor, CheckCache)
    system.scheduler.schedule(FiniteDuration(2, SECONDS), FiniteDuration(20, MILLISECONDS), proxyAssistantActor, CleanUp)
  }

}

object Main extends App {
  val injector = Guice.createInjector(
    new ConfigModule,
    new AkkaModule,
    new ServiceModule,
    new ActorModule,
    new ElemeModule,
    new XiCiDaiLiModule
  )

  import net.codingwell.scalaguice.InjectorExtensions._

  val proxyAssistantBootstrap = injector.instance[ProxyAssistantBootstrap]
  proxyAssistantBootstrap.start()
}
