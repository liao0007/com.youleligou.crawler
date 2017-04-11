package com.youleligou.eleme

import akka.actor._
import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.actors.AbstractInjectActor
import com.youleligou.crawler.actors.AbstractInjectActor._
import com.youleligou.crawler.models.UrlInfo.UrlInfoType
import com.youleligou.crawler.models.{FetchRequest, UrlInfo}
import com.youleligou.eleme.actors.RestaurantInjectActor

import scala.concurrent.duration._

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
  def start(): Unit = {

      injectActor ! AbstractInjectActor.Inject(
      FetchRequest(
        requestName = "fetch_eleme_restaurant",
        urlInfo = UrlInfo(
          host = "http://mainsite-restapi.ele.me/shopping/restaurants",
          queryParameters = Map(
            "latitude"  -> "39.88",
            "longitude" -> "116.45",
            "offset"    -> "9999"
          ),
          urlType = UrlInfoType.Seed
        )
      ))

//    system.scheduler.schedule(0.seconds, 10.millis, injectActor, Tick)
    system.scheduler.scheduleOnce(1.seconds, injectActor, Tick)
    system.scheduler.scheduleOnce(10.seconds, injectActor, Tick)
    system.scheduler.scheduleOnce(10.seconds, injectActor, Tick)
    system.scheduler.scheduleOnce(10.seconds, injectActor, Tick)
    system.scheduler.scheduleOnce(10.seconds, injectActor, Tick)

  }

}
