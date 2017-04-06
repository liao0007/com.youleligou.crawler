package com.youleligou.proxyHunters.youdaili.actors

import akka.actor.ActorRef
import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.youleligou.crawler.actors.{AbstractParseActor, CountActor, IndexActor, NamedActor}
import com.youleligou.crawler.services.ParseService
import com.youleligou.proxyHunters.youdaili.services.ProxyListParseService

/**
  * Created by young.yang on 2016/8/28.
  * 解析任务
  */
class ProxyListParseActor @Inject()(config: Config,
                                    @Named(ProxyListParseService.name) parseService: ParseService,
                                    @Named(IndexActor.poolName) indexActor: ActorRef,
                                    @Named(ProxyListInjectActor.poolName) injectActor: ActorRef,
                                    @Named(CountActor.poolName) countActor: ActorRef)
  extends AbstractParseActor(config, parseService, indexActor, injectActor, countActor)

object ProxyListParseActor extends NamedActor {
  final val name = "YouDaiLiProxyListParseActor"
  final val poolName = "YouDaiLiProxyListParseActorPool"
}
