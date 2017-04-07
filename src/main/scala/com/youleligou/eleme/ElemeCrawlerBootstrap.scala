package com.youleligou.eleme

import akka.actor._
import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.actors.AbstractInjectActor.{GenerateFetch, Init}
import com.youleligou.eleme.actors.RestaurantInjectActor

import scala.concurrent.duration._

/**
  * Created by dell on 2016/8/29.
  * 爬虫主函数
  */
class ElemeCrawlerBootstrap @Inject()(config: Config, system: ActorSystem, @Named(RestaurantInjectActor.poolName) injectActor: ActorRef)
    extends LazyLogging {
  import system.dispatcher

  val timeout       = config.getInt("crawler.actor.proxy-assistant.timeout")
  val fetchParallel = config.getInt("crawler.actor.fetch.parallel")

  /**
    * 爬虫启动函数
    */
  def start(delay: FiniteDuration): Unit = {
    system.scheduler.scheduleOnce(FiniteDuration(0, MILLISECONDS), injectActor, Init)
    system.scheduler.schedule(delay, FiniteDuration(timeout / fetchParallel, MILLISECONDS), injectActor, GenerateFetch)
  }

}
