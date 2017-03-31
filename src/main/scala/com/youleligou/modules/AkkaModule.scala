package com.youleligou.modules

import akka.actor.ActorSystem
import com.google.inject.{AbstractModule, Injector, Provider}
import com.typesafe.config.Config
import net.codingwell.scalaguice.ScalaModule
import javax.inject.Inject

import com.youleligou.modules.AkkaModule.ActorSystemProvider

/**
  * Created by liangliao on 31/3/17.
  */
object AkkaModule {
  class ActorSystemProvider @Inject()(val config: Config) extends Provider[ActorSystem] {
    override def get(): ActorSystem = {
      ActorSystem(config.getString("crawler.appName"), config)
    }
  }
}

/**
  * A module providing an Akka ActorSystem.
  */
class AkkaModule extends AbstractModule with ScalaModule {
  override def configure() {
    bind[ActorSystem].toProvider[ActorSystemProvider].asEagerSingleton()
  }
}
