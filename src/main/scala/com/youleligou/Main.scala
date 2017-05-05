package com.youleligou

import com.google.inject.Guice
import com.youleligou.crawler.CrawlerModule
import com.youleligou.crawler.modules.{ActorModule, AkkaModule, ConfigModule, ServiceModule}
import com.youleligou.eleme.ElemeModule
import com.youleligou.processors.{MenuProcessor, RestaurantProcessor}
import com.youleligou.proxyHunters.xicidaili.XiCiDaiLiModule
import com.youleligou.proxyHunters.youdaili.YouDaiLiModule

/**
  * Created by liangliao on 31/3/17.
  */
object Main extends App {
  val injector = Guice.createInjector(
    new ConfigModule,
    new AkkaModule,
    new ServiceModule,
    new ActorModule,
    new CrawlerModule,
    new ElemeModule,
    new XiCiDaiLiModule,
    new YouDaiLiModule
  )

  import net.codingwell.scalaguice.InjectorExtensions._

  /*
  restaurant
   */
  if (args.contains("eleme/restaurants/start"))
    injector.instance[ElemeCrawlerBootstrap].startRestaurants()

  if (args.contains("eleme/restaurants/clean"))
    injector.instance[ElemeCrawlerBootstrap].cleanRestaurants()

  if (args.contains("eleme/restaurants/reindex"))
    injector.instance[RestaurantProcessor].reindex()

  /*
  menu
   */
  if (args.contains("eleme/menu/start"))
    injector.instance[ElemeCrawlerBootstrap].startMenu()

  if (args.contains("eleme/menu/clean"))
    injector.instance[ElemeCrawlerBootstrap].cleanMenu()
  injector.instance[MenuProcessor].reindex()
  if (args.contains("eleme/menu/reindex"))
    injector.instance[MenuProcessor].reindex()

  /*
  proxy
   */
  if (args.contains("proxy/assistant"))
    injector.instance[ProxyAssistantBootstrap].start()

  if (args.contains("proxy/you"))
    injector.instance[YouDaiLiCrawlerBootstrap].start()

  if (args.contains("proxy/xici"))
    injector.instance[XiCiDaiLiCrawlerBootstrap].start()
}
