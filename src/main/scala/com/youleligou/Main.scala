package com.youleligou

import com.google.inject.Guice
import com.youleligou.crawler.modules.{ActorModule, AkkaModule, ConfigModule, ServiceModule}
import com.youleligou.eleme.ElemeModule
import com.youleligou.proxyHunters.xicidaili.{XiCiDaiLiCrawlerBootstrap, XiCiDaiLiModule}

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

  val xiCiDaiLiCrawlerBootstrap = injector.instance[XiCiDaiLiCrawlerBootstrap]
  xiCiDaiLiCrawlerBootstrap.start()
}
