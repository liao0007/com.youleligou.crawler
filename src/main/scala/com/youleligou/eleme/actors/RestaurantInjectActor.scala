package com.youleligou.eleme.actors

import com.google.inject.Inject
import com.typesafe.config.Config
import com.youleligou.crawler.actors.{AbstractInjectActor, NamedActor}
import com.youleligou.crawler.modules.GuiceAkkaActorRefProvider
import com.youleligou.crawler.services.HashService
import redis.RedisClient

class RestaurantInjectActor @Inject()(config: Config, redisClient: RedisClient, hashService: HashService)
    extends AbstractInjectActor(config, redisClient, hashService, RestaurantFetchActor)
    with GuiceAkkaActorRefProvider {}

object RestaurantInjectActor extends NamedActor {
  final val name     = "ElemeRestaurantInjectActor"
  final val poolName = "ElemeRestaurantInjectActorPool"
}
