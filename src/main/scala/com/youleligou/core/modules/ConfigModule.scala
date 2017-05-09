package com.youleligou.core.modules

import javax.inject.Singleton

import com.google.inject._
import com.typesafe.config.{Config, ConfigFactory}
import com.youleligou.core.services.GeoHash
import com.youleligou.crawler.modules._
import net.codingwell.scalaguice.ScalaModule

/**
  * Created by liangliao on 31/3/17.
  */
class ConfigModule extends AbstractModule with ScalaModule with GuiceAkkaActorRefProvider {

  @Provides
  @Singleton
  def provideConfig: Config = {
    ConfigFactory.load()
  }

  override def configure() {
  }
}
