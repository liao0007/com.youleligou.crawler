package com.youleligou.proxyHunters.xicidaili.actors

import akka.actor.ActorRef
import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.youleligou.crawler.actors.{AbstractInjectActor, NamedActor}
import com.youleligou.crawler.services.{CacheService, HashService}
import redis.RedisClient

class ProxyListInjectActor @Inject()(config: Config,
                                     redisClient: RedisClient,
                                     hashService: HashService,
                                     @Named(ProxyListFetchActor.poolName) fetchActor: ActorRef)
    extends AbstractInjectActor(config, redisClient, hashService, fetchActor)

object ProxyListInjectActor extends NamedActor {
  final val name     = "XiCiDaiLiProxyListInjectActor"
  final val poolName = "XiCiDaiLiProxyListInjectActorPool"
}
