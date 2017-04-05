package com.youleligou.crawler

import akka.actor.ActorRef
import com.google.inject.Guice
import com.youleligou.crawler.actors.ProxyAssistantActor.Hunt
import com.youleligou.crawler.modules.{ActorModule, AkkaModule, ConfigModule, ServiceModule}
import com.youleligou.crawler.proxyHunters.xicidaili.XiCiDaiLiModule
import com.youleligou.eleme.ElemeModule

/**
  * Created by liangliao on 31/3/17.
  */
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

  //  val elemeCrawlerBoot = injector.instance[ElemeCrawlerBootstrap]
  //  elemeCrawlerBoot.start()
  val proxyAssistantActor = injector.instance[ActorRef]
  proxyAssistantActor ! Hunt
}
