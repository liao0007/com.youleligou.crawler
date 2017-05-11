package com.youleligou

import com.google.inject.Guice
import com.youleligou.crawler.modules.DaoModule
import com.youleligou.eleme.ElemeModule
import com.youleligou.meituan.MeituanModule
import com.youleligou.processors.MenuProcessor
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

//  injector.instance[MenuProcessor].reindex()
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

  if (args.contains("eleme/menu/reindex"))
    injector.instance[MenuProcessor].reindex()

  /*
  meituan
   */
  /*
 restaurant
   */

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
 baidu
  */
  /*
 restaurant
   */
  if (args.contains("baidu/restaurants/start"))
    injector.instance[BaiduCrawlerBootstrap].startRestaurants()

  if (args.contains("baidu/restaurants/clean"))
    injector.instance[BaiduCrawlerBootstrap].cleanRestaurants()

  /*
  menu
   */
  if (args.contains("baidu/menu/start"))
    injector.instance[BaiduCrawlerBootstrap].startMenu()

  if (args.contains("baidu/menu/clean"))
    injector.instance[BaiduCrawlerBootstrap].cleanMenu()

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
