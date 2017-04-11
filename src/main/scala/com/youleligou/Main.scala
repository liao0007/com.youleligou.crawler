package com.youleligou

import akka.actor.{ActorRef, ActorSystem}
import com.google.inject.name.Named
import com.google.inject.{Guice, Inject}
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.actors.ProxyAssistantActor
import com.youleligou.crawler.actors.ProxyAssistantActor.{Clean, Init}
import com.youleligou.crawler.modules.{ActorModule, AkkaModule, ConfigModule, ServiceModule}
import com.youleligou.eleme.ElemeModule
import com.youleligou.proxyHunters.xicidaili.{XiCiDaiLiCrawlerBootstrap, XiCiDaiLiModule}

import scala.concurrent.duration._

/**
  * Created by liangliao on 31/3/17.
  */
class ProxyAssistantBootstrap @Inject()(config: Config, system: ActorSystem, @Named(ProxyAssistantActor.name) proxyAssistantActor: ActorRef)
    extends LazyLogging {
  import system.dispatcher
  def start(): Unit = {
    system.scheduler.scheduleOnce(0.second, proxyAssistantActor, Init)
    system.scheduler.schedule(10.second, 20.millis, proxyAssistantActor, Clean)
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

//  injector.instance[ProxyAssistantBootstrap].start()
//  injector.instance[ElemeCrawlerBootstrap].start()
  injector.instance[XiCiDaiLiCrawlerBootstrap].start()

}
