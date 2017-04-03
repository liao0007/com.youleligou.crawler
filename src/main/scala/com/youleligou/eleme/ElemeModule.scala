package com.youleligou.eleme

import com.google.inject.AbstractModule
import com.youleligou.crawler.service.{InjectService, ParseService}
import net.codingwell.scalaguice.ScalaModule

/**
  * Created by liangliao on 31/3/17.
  */
class ElemeModule extends AbstractModule with ScalaModule {

  override def configure() {
    bind[ParseService].annotatedWithName(RestaurantParseService.name).to[RestaurantParseService].asEagerSingleton()
    bind[InjectService].annotatedWithName(RestaurantInjectService.name).to[RestaurantInjectService].asEagerSingleton()
  }

}
