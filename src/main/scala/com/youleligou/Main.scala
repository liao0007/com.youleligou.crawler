package com.youleligou

import akka.actor.{ActorRef, ActorSystem}
import com.google.inject.name.Named
import com.google.inject.{Guice, Inject}
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.actors.ProxyAssistantActor
import com.youleligou.crawler.actors.ProxyAssistantActor.{Clean, Init}
import com.youleligou.crawler.modules.{ActorModule, AkkaModule, ConfigModule, ServiceModule}
import com.youleligou.eleme.{ElemeCrawlerBootstrap, ElemeModule}

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
    new ElemeModule
  )

  import net.codingwell.scalaguice.InjectorExtensions._

  injector.instance[ProxyAssistantBootstrap].start()
  //min interval: 2000/100 = 20 milliseconds
  injector.instance[ElemeCrawlerBootstrap].start()

//  injector.instance[ProxyAssistantBootstrap].start(FiniteDuration(5000000, MILLISECONDS))
//  injector.instance[ElemeCrawlerBootstrap].start(FiniteDuration(3, SECONDS), FiniteDuration(3, SECONDS))
}
