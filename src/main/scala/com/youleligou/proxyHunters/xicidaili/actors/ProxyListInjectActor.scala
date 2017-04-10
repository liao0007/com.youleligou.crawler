package com.youleligou.proxyHunters.xicidaili.actors

import akka.actor.ActorRef
import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.youleligou.crawler.actors.{AbstractInjectActor, NamedActor}
import com.youleligou.crawler.services.{CacheService, HashService}

class ProxyListInjectActor @Inject()(config: Config,
                                     cacheService: CacheService,
                                     hashService: HashService,
                                     @Named(ProxyListFetchActor.poolName) fetchActor: ActorRef)
    extends AbstractInjectActor(config, cacheService, hashService, fetchActor)

object ProxyListInjectActor extends NamedActor {
  final val name     = "XiCiDaiLiProxyListInjectActor"
  final val poolName = "XiCiDaiLiProxyListInjectActorPool"
}
