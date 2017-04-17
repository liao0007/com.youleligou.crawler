package com.youleligou.proxyHunters.xicidaili

import com.google.inject._
import com.youleligou.crawler.modules._
import com.youleligou.crawler.services.ParseService
import com.youleligou.proxyHunters.xicidaili.services.proxyList
import net.codingwell.scalaguice.ScalaModule

/**
  * Created by liangliao on 31/3/17.
  */
class XiCiDaiLiModule extends AbstractModule with ScalaModule with GuiceAkkaActorRefProvider {

  override def configure() {
    bind[ParseService].annotatedWithName(classOf[proxyList.ParseService].getName).to[proxyList.ParseService]
  }
}
