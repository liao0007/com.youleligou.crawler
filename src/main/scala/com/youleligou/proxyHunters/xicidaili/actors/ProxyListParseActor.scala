package com.youleligou.proxyHunters.xicidaili.actors

import akka.actor.ActorRef
import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.youleligou.crawler.actors.{AbstractParseActor, IndexActor, NamedActor}
import com.youleligou.crawler.services.ParseService
import com.youleligou.proxyHunters.xicidaili.services.ProxyListParseService

/**
  * Created by young.yang on 2016/8/28.
  * 解析任务
  */
class ProxyListParseActor @Inject()(config: Config,
                                    @Named(ProxyListParseService.name) parseService: ParseService,
                                    @Named(IndexActor.poolName) indexerPool: ActorRef,
                                    @Named(ProxyListInjectActor.poolName) injectorPool: ActorRef)
    extends AbstractParseActor(config, parseService, indexerPool, injectorPool)

object ProxyListParseActor extends NamedActor {
  final val name     = "XiCiDaiLiProxyListParseActor"
  final val poolName = "XiCiDaiLiProxyListParseActorPool"
}
