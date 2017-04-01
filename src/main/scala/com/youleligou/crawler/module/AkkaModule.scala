package com.youleligou.crawler.module

import akka.actor.{Actor, ActorRef, ActorSystem, ExtendedActorSystem, Extension, ExtensionId, ExtensionIdProvider, IndirectActorProducer, Props}
import com.google.inject.name.Names
import com.google.inject._
import com.typesafe.config.Config
import net.codingwell.scalaguice.ScalaModule

class GuiceActorProducer(injector: Injector, actorName: String) extends IndirectActorProducer {
  override def actorClass: Class[Actor] = classOf[Actor]
  override def produce(): Actor         = injector.getBinding(Key.get(classOf[Actor], Names.named(actorName))).getProvider.get()
}

class GuiceAkkaExtensionImpl extends Extension {
  private var injector: Injector = _

  def initialize(injector: Injector): Unit = {
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
/**
  * A module providing an Akka ActorSystem.
  */
class AkkaModule extends AbstractModule with ScalaModule {

  @Provides
  @Singleton
  def providesActorSystem(config: Config, injector: Injector): ActorSystem = {
    val system = ActorSystem(config.getString("crawler.appName"), config)
    GuiceAkkaExtension(system).initialize(injector)
    system
  }

  override def configure() {}
}
