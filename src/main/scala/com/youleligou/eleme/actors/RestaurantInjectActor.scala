package com.youleligou.eleme.actors

import akka.actor.ActorRef
import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.youleligou.crawler.actors.{AbstractInjectActor, NamedActor}
import com.youleligou.crawler.services.{CacheService, HashService}
import redis.RedisClient

class RestaurantInjectActor @Inject()(config: Config,
                                      redisClient: RedisClient,
                                      hashService: HashService,
                                      @Named(RestaurantFetchActor.poolName) fetchActor: ActorRef)
    extends AbstractInjectActor(config, redisClient, hashService, fetchActor)

object RestaurantInjectActor extends NamedActor {
  final val name     = "ElemeRestaurantInjectActor"
  final val poolName = "ElemeRestaurantInjectActorPool"
}
