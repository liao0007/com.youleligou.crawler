package com.youleligou.eleme.actors

import akka.actor.ActorRef
import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.youleligou.crawler.actors.{AbstractFetchActor, NamedActor, ProxyAssistantActor}
import com.youleligou.crawler.services.FetchService

class RestaurantFetchActor @Inject()(config: Config,
                                     fetchService: FetchService,
                                     @Named(RestaurantInjectActor.poolName) injectorPool: ActorRef,
                                     @Named(ProxyAssistantActor.poolName) proxyAssistantPool: ActorRef)
    extends AbstractFetchActor(config, fetchService, injectorPool, proxyAssistantPool, RestaurantParseActor) {}

object RestaurantFetchActor extends NamedActor {
  final val name     = "ElemeRestaurantFetchActor"
  final val poolName = "ElemeRestaurantFetchActorPool"

}
