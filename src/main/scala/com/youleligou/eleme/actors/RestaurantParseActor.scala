package com.youleligou.eleme.actors

import akka.actor.ActorRef
import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.youleligou.crawler.actors.{AbstractParseActor, IndexActor, NamedActor}
import com.youleligou.crawler.services.ParseService
import com.youleligou.eleme.services.RestaurantParseService

/**
  * Created by young.yang on 2016/8/28.
  * 解析任务
  */
class RestaurantParseActor @Inject()(config: Config,
                                     @Named(RestaurantParseService.name) parseService: ParseService,
                                     @Named(IndexActor.poolName) indexerPool: ActorRef,
                                     @Named(RestaurantInjectActor.poolName) injectorPool: ActorRef)
    extends AbstractParseActor(config, parseService, indexerPool, injectorPool)

object RestaurantParseActor extends NamedActor {
  final val name     = "ElemeRestaurantParseActor"
  final val poolName = "ElemeRestaurantParseActorPool"
}
