package com.youleligou.crawler.proxyHunters.xicidaili.actors

import akka.actor.ActorRef
import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.youleligou.crawler.actors.{AbstractInjectActor, CountActor, NamedActor}
import com.youleligou.crawler.proxyHunters.xicidaili.services.ProxyListInjectService
import com.youleligou.crawler.services.{CacheService, FilterService, HashService, InjectService}

class ProxyListInjectActor @Inject()(config: Config,
                                     cacheService: CacheService,
                                     hashService: HashService,
                                     filterService: FilterService,
                                     @Named(ProxyListInjectService.name) injectService: InjectService,
                                     @Named(ProxyListFetchActor.poolName) fetchActor: ActorRef,
                                     @Named(CountActor.poolName) countActor: ActorRef)
  extends AbstractInjectActor(config, cacheService, hashService, filterService, injectService, fetchActor, countActor)

object ProxyListInjectActor extends NamedActor {
  override final val name: String = "XiCiDaiLiProxyList" + "InjectActor"
  override final val poolName: String = "XiCiDaiLiProxyList" + "InjectActorPool"
}
