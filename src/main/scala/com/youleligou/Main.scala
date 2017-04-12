package com.youleligou

import akka.actor.{ActorRef, ActorSystem}
import com.google.inject.name.Named
import com.google.inject.{Guice, Inject}
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.actors.ProxyAssistantActor.{Init, Replenish}
import com.youleligou.crawler.actors.ProxyReplenishmentAssistantActor
import com.youleligou.crawler.modules.{ActorModule, AkkaModule, ConfigModule, ServiceModule}
import com.youleligou.eleme.{ElemeCrawlerBootstrap, ElemeModule}
import com.youleligou.proxyHunters.xicidaili.XiCiDaiLiModule
import com.youleligou.proxyHunters.youdaili.{YouDaiLiCrawlerBootstrap, YouDaiLiModule}

import scala.concurrent.duration._

/**
  * Created by liangliao on 31/3/17.
  */
class ProxyReplenishmentAssistantBootstrap @Inject()(config: Config,
                                                     system: ActorSystem,
                                                     @Named(ProxyReplenishmentAssistantActor.poolName) proxyAssistantActor: ActorRef)
    extends LazyLogging {
  import system.dispatcher
  def start(): Unit = {
    system.scheduler.scheduleOnce(0.second, proxyAssistantActor, Init)
    system.scheduler.schedule(10.second, 20.millis, proxyAssistantActor, Replenish)
  }
}

object Main extends App {
  val injector = Guice.createInjector(
    new ConfigModule,
    new AkkaModule,
    new ServiceModule,
    new ActorModule,
    new ElemeModule,
    new XiCiDaiLiModule,
    new YouDaiLiModule
  )

  import net.codingwell.scalaguice.InjectorExtensions._

  if (args.contains("proxy"))
    injector.instance[ProxyReplenishmentAssistantBootstrap].start()

  if (args.contains("eleme"))
    injector.instance[ElemeCrawlerBootstrap].start()

  if (args.contains("youdaili"))
    injector.instance[YouDaiLiCrawlerBootstrap].start()

  if (args.contains("xicidaili"))
    injector.instance[YouDaiLiCrawlerBootstrap].start()
}
