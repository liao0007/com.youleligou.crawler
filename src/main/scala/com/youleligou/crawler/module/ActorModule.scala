package com.youleligou.crawler.module

import akka.actor.{Actor, ActorRef, ActorSystem}
import com.google.inject.{AbstractModule, Provides}
import com.google.inject.name.{Named, Names}
import com.youleligou.crawler.actor._
import net.codingwell.scalaguice.ScalaModule

/**
  * Created by liangliao on 1/4/17.
  */
class ActorModule extends AbstractModule with ScalaModule with GuiceAkkaActorRefProvider {

  /**
    * provide Actors
    */
  @Provides
  @Named(InjectActor.name)
  def provideInjectActorRef(system: ActorSystem): ActorRef = provideActorRef(system, InjectActor.name)

  @Provides
  @Named(CountActor.name)
  def provideAuditActorRef(system: ActorSystem): ActorRef = provideActorRef(system, CountActor.name)

  @Provides
  @Named(FetchActor.name)
  def provideFetchActorRef(system: ActorSystem): ActorRef = provideActorRef(system, FetchActor.name)

  @Provides
  @Named(ParseActor.name)
  def provideParseActorRef(system: ActorSystem): ActorRef = provideActorRef(system, ParseActor.name)

  @Provides
  @Named(IndexActor.name)
  def provideIndexActorRef(system: ActorSystem): ActorRef = provideActorRef(system, IndexActor.name)

  override def configure() {
    bind[Actor].annotatedWith(Names.named(InjectActor.name)).to[InjectActor]
    bind[Actor].annotatedWith(Names.named(CountActor.name)).to[CountActor]
    bind[Actor].annotatedWith(Names.named(FetchActor.name)).to[FetchActor]
    bind[Actor].annotatedWith(Names.named(ParseActor.name)).to[ParseActor]
    bind[Actor].annotatedWith(Names.named(IndexActor.name)).to[IndexActor]
  }
}