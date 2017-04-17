package com.youleligou.proxyHunters.youdaili

import com.google.inject._
import com.youleligou.crawler.modules._
import com.youleligou.crawler.services.ParseService
import com.youleligou.proxyHunters.youdaili.services.{proxyList, proxyPage}
import net.codingwell.scalaguice.ScalaModule

/**
  * Created by liangliao on 31/3/17.
  */
class YouDaiLiModule extends AbstractModule with ScalaModule with GuiceAkkaActorRefProvider {

  override def configure() {
    bind[ParseService].annotatedWithName(classOf[proxyList.ParseService].getName).to[proxyList.ParseService]
    bind[ParseService].annotatedWithName(classOf[proxyPage.ParseService].getName).to[proxyPage.ParseService]
  }
}
