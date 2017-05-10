package com.youleligou.core.modules

import com.google.inject._
import com.youleligou.core.services.GeoHash
import net.codingwell.scalaguice.ScalaModule

/**
  * Created by liangliao on 31/3/17.
  */
class ServiceModule extends AbstractModule with ScalaModule with GuiceAkkaActorRefProvider {

  override def configure() {
    bind[GeoHash]
  }
}
