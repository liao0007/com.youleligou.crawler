package com.youleligou.crawler.modules

import javax.inject.Inject

import akka.actor.{Actor, ActorRef, ActorSystem, ExtendedActorSystem, Extension, ExtensionId, ExtensionIdProvider, IndirectActorProducer, Props}
import com.google.inject.name.Names
import com.google.inject.{AbstractModule, Injector, Key, Provider}
import com.typesafe.config.Config
import com.youleligou.crawler.modules.AkkaModule.ActorSystemProvider
import net.codingwell.scalaguice.ScalaModule


class GuiceActorProducer(val injector: Injector, val actorName: String) extends IndirectActorProducer {
  override def actorClass: Class[Actor] = classOf[Actor]

  override def produce(): Actor = injector.getBinding(Key.get(classOf[Actor], Names.named(actorName))).getProvider.get()
}

class GuiceAkkaExtensionImpl extends Extension {
  private var injector: Injector = _

  def initialize(injector: Injector) {
    this.injector = injector
  }

  def props(actorName: String) = Props(classOf[GuiceActorProducer], injector, actorName)
}

object GuiceAkkaExtension extends ExtensionId[GuiceAkkaExtensionImpl] with ExtensionIdProvider {

  /** Register ourselves with the ExtensionIdProvider */
  override def lookup() = GuiceAkkaExtension

  /** Called by Akka in order to create an instance of the extension. */
  override def createExtension(system: ExtendedActorSystem) = new GuiceAkkaExtensionImpl

  /** Java API: Retrieve the extension for the given system. */
  override def get(system: ActorSystem): GuiceAkkaExtensionImpl = super.get(system)
}

/**
  * Mix in with Guice Modules that contain providers for top-level actor refs.
  */
trait GuiceAkkaActorRefProvider {
  def propsFor(system: ActorSystem, name: String): Props = GuiceAkkaExtension(system).props(name)

  def provideActorRef(system: ActorSystem, name: String): ActorRef = system.actorOf(propsFor(system, name))
}

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
