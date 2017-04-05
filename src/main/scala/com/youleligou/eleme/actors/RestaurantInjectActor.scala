package com.youleligou.eleme.actors

import akka.actor.ActorRef
import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.youleligou.crawler.actors.{AbstractInjectActor, CountActor, FetchActor, NamedActor}
import com.youleligou.crawler.services.{CacheService, FilterService, HashService, InjectService}
import com.youleligou.eleme.services.RestaurantInjectService

class RestaurantInjectActor @Inject()(config: Config,
                                      cacheService: CacheService,
                                      hashService: HashService,
                                      filterService: FilterService,
                                      @Named(RestaurantInjectService.name) injectService: InjectService,
                                      @Named(FetchActor.poolName) fetchActor: ActorRef,
                                      @Named(CountActor.poolName) countActor: ActorRef)
  extends AbstractInjectActor(config, cacheService, hashService, filterService, injectService, fetchActor, countActor)

object RestaurantInjectActor extends NamedActor {
  override final val name = "ElemeRestaurantInjectActor"
  override final val poolName = "ElemeRestaurantInjectActorPool"
}
