package com.youleligou.eleme.actors

import akka.actor.ActorRef
import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.youleligou.crawler.actors.{AbstractParseActor, CountActor, IndexActor, NamedActor}
import com.youleligou.crawler.services.ParseService
import com.youleligou.eleme.services.RestaurantParseService

/**
  * Created by young.yang on 2016/8/28.
  * 解析任务
  */
class RestaurantParseActor @Inject()(config: Config,
                                     @Named(RestaurantParseService.name) parseService: ParseService,
                                     @Named(IndexActor.poolName) indexActor: ActorRef,
                                     @Named(RestaurantInjectActor.poolName) injectActor: ActorRef,
                                     @Named(CountActor.poolName) countActor: ActorRef)
  extends AbstractParseActor(config, parseService, indexActor, injectActor, countActor)

object RestaurantParseActor extends NamedActor {
  override final val name: String = "ElemeRestaurant" + "ParseActor"
  override final val poolName: String = "ElemeRestaurant" + "ParseActorPool"
}
