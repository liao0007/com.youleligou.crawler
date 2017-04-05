package com.youleligou.crawler.modules

import com.google.inject.AbstractModule
import com.typesafe.config.{Config, ConfigFactory}
import net.codingwell.scalaguice.ScalaModule

/**
  * Created by liangliao on 31/3/17.
  */
/**
  * Binds the application configuration to the [[Config]] interface.
  *
  * The config is bound as an eager singleton so that errors in the config are detected
  * as early as possible.
  */
class ConfigModule extends AbstractModule with ScalaModule {
  override def configure() {
    bind[Config].toInstance(ConfigFactory.load())
  }
}
