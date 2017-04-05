package com.youleligou.crawler.proxyHunters.xicidaili.actors

import akka.actor.ActorRef
import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.youleligou.crawler.actors.{AbstractInjectActor, CountActor, FetchActor, NamedActor}
import com.youleligou.crawler.proxyHunters.xicidaili.services.ProxyListInjectService
import com.youleligou.crawler.services.{CacheService, FilterService, HashService, InjectService}

class ProxyListInjectActor @Inject()(config: Config,
                                     cacheService: CacheService,
                                     hashService: HashService,
                                     filterService: FilterService,
                                     @Named(ProxyListInjectService.name) injectService: InjectService,
                                     @Named(FetchActor.poolName) fetchActor: ActorRef,
                                     @Named(CountActor.poolName) countActor: ActorRef)
  extends AbstractInjectActor(config, cacheService, hashService, filterService, injectService, fetchActor, countActor)

object ProxyListInjectActor extends NamedActor {
  override final val name = "XiCiProxyListInjectActor"
  override final val poolName = "XiCiProxyListInjectActorPool"
}
