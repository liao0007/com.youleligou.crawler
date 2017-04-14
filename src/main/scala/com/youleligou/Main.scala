package com.youleligou

import akka.actor.{ActorRef, ActorSystem}
import com.google.inject.name.Named
import com.google.inject.{Guice, Inject}
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.actors.ProxyAssistantActor
import com.youleligou.crawler.actors.ProxyAssistantActor.Run
import com.youleligou.crawler.modules.{ActorModule, AkkaModule, ConfigModule, ServiceModule}
import com.youleligou.eleme.{ElemeCrawlerBootstrap, ElemeModule}
import com.youleligou.proxyHunters.xicidaili.{XiCiDaiLiCrawlerBootstrap, XiCiDaiLiModule}
import com.youleligou.proxyHunters.youdaili.{YouDaiLiCrawlerBootstrap, YouDaiLiModule}

import scala.concurrent.duration._

/**
  * Created by liangliao on 31/3/17.
  */
class ProxyAssistantBootstrap @Inject()(config: Config, system: ActorSystem, @Named(ProxyAssistantActor.name) proxyAssistantActor: ActorRef)
    extends LazyLogging {
  import system.dispatcher
  def start(): Unit = {
    system.scheduler.schedule(0.second, FiniteDuration(config.getInt("crawler.proxy-assistant.interval"), MILLISECONDS), proxyAssistantActor, Run)
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
  injector.instance[ElemeCrawlerBootstrap].startFood()
  if (args.contains("eleme/restaurant"))
    injector.instance[ElemeCrawlerBootstrap].startRestaurant()

  if (args.contains("eleme/food"))
    injector.instance[ElemeCrawlerBootstrap].startFood()

  if (args.contains("proxy/assistant"))
    injector.instance[ProxyAssistantBootstrap].start()

  if (args.contains("proxy/you"))
    injector.instance[YouDaiLiCrawlerBootstrap].start()

  if (args.contains("proxy/xici"))
    injector.instance[XiCiDaiLiCrawlerBootstrap].start()
}

//object Main2 extends App {
//  val config = ConfigFactory.load()
//
//  val system = ActorSystem(config.getString("appName"), config)
//
//}
