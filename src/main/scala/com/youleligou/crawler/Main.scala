package com.youleligou.crawler

import com.google.inject.Guice
import com.youleligou.crawler.boot.CrawlerBoot
import com.youleligou.crawler.module.{ActorModule, AkkaModule, ServiceModule, ConfigModule}

/**
  * Created by liangliao on 31/3/17.
  */
object Main extends App {
  val injector = Guice.createInjector(
    new ConfigModule,
    new AkkaModule,
    new ServiceModule,
    new ActorModule
  )

  import net.codingwell.scalaguice.InjectorExtensions._

  val crawlerBoot = injector.instance[CrawlerBoot]
  crawlerBoot.start()
}
