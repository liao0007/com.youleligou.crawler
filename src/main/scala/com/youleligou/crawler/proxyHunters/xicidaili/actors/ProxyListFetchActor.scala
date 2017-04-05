package com.youleligou.crawler.proxyHunters.xicidaili.actors

import akka.actor.ActorRef
import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.youleligou.crawler.actors.{AbstractFetchActor, CountActor, NamedActor}
import com.youleligou.crawler.services.FetchService

class ProxyListFetchActor @Inject()(config: Config,
                                    fetchService: FetchService,
                                    @Named(ProxyListParseActor.poolName) parserActor: ActorRef,
                                    @Named(CountActor.poolName) countActor: ActorRef)
  extends AbstractFetchActor(config, fetchService, parserActor, countActor)

object ProxyListFetchActor extends NamedActor {
  override final val name: String = "XiCiDaiLiProxyList" + "FetchActor"
  override final val poolName: String = "XiCiDaiLiProxyList" + "FetchActorPool"
}
