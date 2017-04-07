package com.youleligou

import akka.actor.{ActorRef, ActorSystem}
import com.google.inject.name.Named
import com.google.inject.{Guice, Inject}
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.actors.ProxyAssistantActor
import com.youleligou.crawler.actors.ProxyAssistantActor.{CheckCache, Clean}
import com.youleligou.crawler.modules.{ActorModule, AkkaModule, ConfigModule, ServiceModule}
import com.youleligou.eleme.ElemeModule
import com.youleligou.proxyHunters.xicidaili.XiCiDaiLiModule

import scala.concurrent.duration._

/**
  * Created by liangliao on 31/3/17.
  */
class ProxyAssistantBootstrap @Inject()(config: Config,
                                        system: ActorSystem,
                                        @Named(ProxyAssistantActor.poolName) proxyAssistantActor: ActorRef)
    extends LazyLogging {
  import system.dispatcher

  val timeout        = config.getInt("crawler.actor.proxy-assistant.timeout")
  val assistantDelta = config.getInt("crawler.actor.proxy-assistant.parallel") - config.getInt("crawler.actor.fetch.parallel")

  def start(): Unit = {
    system.scheduler.scheduleOnce(FiniteDuration(0, MILLISECONDS), proxyAssistantActor, CheckCache)
    system.scheduler.schedule(FiniteDuration(5, SECONDS), FiniteDuration(timeout / assistantDelta, MILLISECONDS), proxyAssistantActor, Clean)
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

  injector.instance[ProxyAssistantBootstrap].start()

  //min interval: 2000/100 = 20 milliseconds
//  injector.instance[ElemeCrawlerBootstrap].start(FiniteDuration(5, SECONDS), FiniteDuration(1000, MILLISECONDS))
//  injector.instance[ProxyAssistantBootstrap].start(FiniteDuration(5000000, MILLISECONDS))
//  injector.instance[ElemeCrawlerBootstrap].start(FiniteDuration(3, SECONDS), FiniteDuration(3, SECONDS))
}
