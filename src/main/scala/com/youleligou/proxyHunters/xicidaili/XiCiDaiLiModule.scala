package com.youleligou.proxyHunters.xicidaili

import com.google.inject._
import com.youleligou.crawler.modules._
import com.youleligou.crawler.services.ParseService
import com.youleligou.proxyHunters.xicidaili.services.{parse, proxyList}
import net.codingwell.scalaguice.ScalaModule

/**
  * Created by liangliao on 31/3/17.
  */
class XiCiDaiLiModule extends AbstractModule with ScalaModule with GuiceAkkaActorRefProvider {

  override def configure() {
    bind[ParseService].annotatedWithName(classOf[parse.ProxyListParseService].getName).to[parse.ProxyListParseService]
  }
}
