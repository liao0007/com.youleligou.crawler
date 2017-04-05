package com.youleligou.eleme.actors

import akka.actor.ActorRef
import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.youleligou.crawler.actors.{AbstractFetchActor, CountActor, NamedActor, ProxyAssistantActor}
import com.youleligou.crawler.services.FetchService

class RestaurantFetchActor @Inject()(config: Config,
                                     fetchService: FetchService,
                                     @Named(RestaurantParseActor.poolName) parserActor: ActorRef,
                                     @Named(CountActor.poolName) countActor: ActorRef,
                                     @Named(ProxyAssistantActor.poolName) proxyAssistantActor: ActorRef)
  extends AbstractFetchActor(config, fetchService, parserActor, countActor, proxyAssistantActor)

object RestaurantFetchActor extends NamedActor {
  final val name = "ElemeRestaurantFetchActor"
  final val poolName = "ElemeRestaurantFetchActorPool"
}
