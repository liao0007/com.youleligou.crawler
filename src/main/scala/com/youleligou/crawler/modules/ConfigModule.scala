package com.youleligou.crawler.modules

import com.google.inject.{AbstractModule, Provider}
import com.typesafe.config.{Config, ConfigFactory}
import com.youleligou.crawler.modules.ConfigModule.ConfigProvider
import net.codingwell.scalaguice.ScalaModule

/**
  * Created by liangliao on 31/3/17.
  */
object ConfigModule {
  class ConfigProvider extends Provider[Config] {
    override def get(): Config = ConfigFactory.load()
  }
}

/**
  * Binds the application configuration to the [[Config]] interface.
  *
  * The config is bound as an eager singleton so that errors in the config are detected
  * as early as possible.
  */
class ConfigModule extends AbstractModule with ScalaModule {
  override def configure() {
    bind[Config].toProvider[ConfigProvider].asEagerSingleton()
  }
}
