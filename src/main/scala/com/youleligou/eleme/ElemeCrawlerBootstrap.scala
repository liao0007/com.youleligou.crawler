package com.youleligou.eleme

import akka.actor._
import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.actors.AbstractInjectActor.GenerateFetch
import com.youleligou.eleme.actors.RestaurantInjectActor

import scala.concurrent.duration.FiniteDuration

/**
  * Created by dell on 2016/8/29.
  * 爬虫主函数
  */
class ElemeCrawlerBootstrap @Inject()(config: Config, system: ActorSystem, @Named(RestaurantInjectActor.poolName) injectActor: ActorRef)
  extends LazyLogging {

  import system.dispatcher

  /**
    * 爬虫启动函数
    */
  def start(delay: FiniteDuration, interval: FiniteDuration): Unit = {
    system.scheduler.schedule(delay, interval, injectActor, GenerateFetch)
  }

}
