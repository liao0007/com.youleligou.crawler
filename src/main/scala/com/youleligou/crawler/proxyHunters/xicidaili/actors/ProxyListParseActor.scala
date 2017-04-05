package com.youleligou.crawler.proxyHunters.xicidaili.actors

import akka.actor.ActorRef
import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.youleligou.crawler.actors.{AbstractParseActor, CountActor, IndexActor, NamedActor}
import com.youleligou.crawler.proxyHunters.xicidaili.services.ProxyListParseService
import com.youleligou.crawler.services.ParseService

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
  override final val name: String = "XiCiDaiLiProxyList" + "ParseActor"
  override final val poolName: String = "XiCiDaiLiProxyList" + "ParseActorPool"
}
