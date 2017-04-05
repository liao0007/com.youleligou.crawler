package com.youleligou.eleme

import akka.actor._
import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import com.youleligou.eleme.actors.RestaurantInjectActor

/**
  * Created by dell on 2016/8/29.
  * 爬虫主函数
  */
class ElemeCrawlerBootstrap @Inject()(config: Config, system: ActorSystem, @Named(RestaurantInjectActor.poolName) injectActor: ActorRef)
  extends LazyLogging {

  /**
    * 爬虫启动函数
    */
  def start(): Unit = {}

}
