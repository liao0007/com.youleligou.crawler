package com.youleligou.crawler

import com.google.inject.Guice
import com.youleligou.crawler.module.{ActorModule, AkkaModule, ConfigModule, ServiceModule}
import com.youleligou.eleme.{ElemeCrawlerBootstrap, ElemeModule}

/**
  * Created by liangliao on 31/3/17.
  */
object Main extends App {
  val injector = Guice.createInjector(
    new ConfigModule,
    new AkkaModule,
    new ServiceModule,
    new ActorModule,
    new ElemeModule
  )

  import net.codingwell.scalaguice.InjectorExtensions._

  val elemeCrawlerBoot = injector.instance[ElemeCrawlerBootstrap]
  elemeCrawlerBoot.start()
}
