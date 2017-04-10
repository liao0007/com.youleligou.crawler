package com.youleligou.eleme.actors

import akka.actor.ActorRef
import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.youleligou.crawler.actors.{AbstractInjectActor, NamedActor}
import com.youleligou.crawler.services.{CacheService, HashService}

class RestaurantInjectActor @Inject()(config: Config,
                                      cacheService: CacheService,
                                      hashService: HashService,
                                      @Named(RestaurantFetchActor.poolName) fetchActor: ActorRef)
    extends AbstractInjectActor(config, cacheService, hashService, fetchActor)

object RestaurantInjectActor extends NamedActor {
  final val name     = "ElemeRestaurantInjectActor"
  final val poolName = "ElemeRestaurantInjectActorPool"
}
