package com.youleligou.eleme.actors

import akka.actor.ActorRef
import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.youleligou.crawler.actors.{AbstractInjectActor, CountActor, NamedActor, ProxyAssistantActor}
import com.youleligou.crawler.services.{CacheService, FilterService, HashService, InjectService}
import com.youleligou.eleme.services.RestaurantInjectService

class RestaurantInjectActor @Inject()(config: Config,
                                      cacheService: CacheService,
                                      hashService: HashService,
                                      filterService: FilterService,
                                      @Named(RestaurantInjectService.name) injectService: InjectService,
                                      @Named(RestaurantFetchActor.poolName) fetchActor: ActorRef,
                                      @Named(ProxyAssistantActor.poolName) proxyAssistantActor: ActorRef,
                                      @Named(CountActor.poolName) countActor: ActorRef)
    extends AbstractInjectActor(config, cacheService, hashService, filterService, injectService, fetchActor, proxyAssistantActor, countActor)

object RestaurantInjectActor extends NamedActor {
  final val name     = "ElemeRestaurantInjectActor"
  final val poolName = "ElemeRestaurantInjectActorPool"
}
