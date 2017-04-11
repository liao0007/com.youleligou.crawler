package com.youleligou.proxyHunters.xicidaili.actors

import com.google.inject.Inject
import com.typesafe.config.Config
import com.youleligou.crawler.actors.{AbstractInjectActor, NamedActor}
import com.youleligou.crawler.services.HashService
import redis.RedisClient

class ProxyListInjectActor @Inject()(config: Config, redisClient: RedisClient, hashService: HashService)
    extends AbstractInjectActor(config, redisClient, hashService, ProxyListFetchActor)

object ProxyListInjectActor extends NamedActor {
  final val name     = "XiCiDaiLiProxyListInjectActor"
  final val poolName = "XiCiDaiLiProxyListInjectActorPool"
}
