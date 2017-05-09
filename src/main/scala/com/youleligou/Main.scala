package com.youleligou

import com.google.inject.Guice
import com.youleligou._
import com.youleligou.crawler.modules.DaoModule
import com.youleligou.eleme.ElemeModule
import com.youleligou.meituan.MeituanModule
import com.youleligou.proxyHunters.xicidaili.XiCiDaiLiModule
import com.youleligou.proxyHunters.youdaili.YouDaiLiModule

/**
  * Created by liangliao on 31/3/17.
  */
object Main extends App {
  val injector = Guice.createInjector(
    new core.modules.ConfigModule,
    new core.modules.AkkaModule,
    new core.modules.ServiceModule,
    new crawler.modules.ActorModule,
    new crawler.modules.ServiceModule,
    new crawler.modules.DaoModule,
    new DaoModule,
    new ElemeModule,
    new MeituanModule,
    new XiCiDaiLiModule,
    new YouDaiLiModule
  )

  import net.codingwell.scalaguice.InjectorExtensions._

//  injector.instance[MeituanCrawlerBootstrap].cleanRestaurants()
//  injector.instance[MeituanCrawlerBootstrap].startRestaurants()

  /*
  eleme
   */
  /*
  restaurant
   */
  if (args.contains("eleme/restaurants/start"))
    injector.instance[ElemeCrawlerBootstrap].startRestaurants()

  if (args.contains("eleme/restaurants/clean"))
    injector.instance[ElemeCrawlerBootstrap].cleanRestaurants()

  /*
  menu
   */
  if (args.contains("eleme/menu/start"))
    injector.instance[ElemeCrawlerBootstrap].startMenu()

  if (args.contains("eleme/menu/clean"))
    injector.instance[ElemeCrawlerBootstrap].cleanMenu()


  /*
  meituan
   */
  /*
 restaurant
  */
//  injector.instance[MeituanCrawlerBootstrap].cleanRestaurants()
//  injector.instance[MeituanCrawlerBootstrap].startRestaurants()

  if (args.contains("meituan/restaurants/start"))
    injector.instance[MeituanCrawlerBootstrap].startRestaurants()

  if (args.contains("meituan/restaurants/clean"))
    injector.instance[MeituanCrawlerBootstrap].cleanRestaurants()

  /*
  menu
   */
  if (args.contains("meituan/menu/start"))
    injector.instance[MeituanCrawlerBootstrap].startMenu()

  if (args.contains("meituan/menu/clean"))
    injector.instance[MeituanCrawlerBootstrap].cleanMenu()



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
