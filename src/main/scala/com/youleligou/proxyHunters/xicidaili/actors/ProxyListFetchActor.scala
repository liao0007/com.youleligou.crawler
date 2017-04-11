package com.youleligou.proxyHunters.xicidaili.actors

import akka.actor.ActorRef
import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.youleligou.crawler.actors.{AbstractFetchActor, NamedActor, ProxyAssistantActor}
import com.youleligou.crawler.services.FetchService

class ProxyListFetchActor @Inject()(config: Config,
                                    fetchService: FetchService,
                                    @Named(ProxyListInjectActor.poolName) injectorPool: ActorRef,
                                    @Named(ProxyAssistantActor.poolName) proxyAssistantPool: ActorRef)
    extends AbstractFetchActor(config, fetchService, injectorPool, proxyAssistantPool, ProxyListParseActor)

object ProxyListFetchActor extends NamedActor {
  final val name     = "XiCiDaiLiProxyListFetchActor"
  final val poolName = "XiCiDaiLiProxyListFetchActorPool"
}
