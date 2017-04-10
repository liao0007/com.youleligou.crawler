package com.youleligou.proxyHunters.xicidaili.actors

import akka.actor.ActorRef
import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.youleligou.crawler.actors.{AbstractFetchActor, CountActor, NamedActor, ProxyAssistantActor}
import com.youleligou.crawler.services.FetchService

class ProxyListFetchActor @Inject()(config: Config,
                                    fetchService: FetchService,
                                    @Named(ProxyListInjectActor.poolName) injectActor: ActorRef,
                                    @Named(ProxyListParseActor.poolName) parseActor: ActorRef,
                                    @Named(ProxyAssistantActor.poolName) proxyAssistantActor: ActorRef)
    extends AbstractFetchActor(config, fetchService, injectActor, parseActor, proxyAssistantActor)

object ProxyListFetchActor extends NamedActor {
  final val name     = "XiCiDaiLiProxyListFetchActor"
  final val poolName = "XiCiDaiLiProxyListFetchActorPool"
}
