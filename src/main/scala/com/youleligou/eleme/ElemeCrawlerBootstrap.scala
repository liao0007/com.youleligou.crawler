package com.youleligou.eleme

import akka.actor._
import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.actors.AbstractInjectActor
import com.youleligou.crawler.models.{FetchRequest, UrlInfo}
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
  def start(): Unit = {
    import com.github.andr83.scalaconfig._
    val seeds = config.as[Seq[UrlInfo]]("crawler.seed.eleme.restaurant-list")
    seeds.foreach { seed =>
      injectActor ! AbstractInjectActor.Inject(
        FetchRequest(
          requestName = "fetch_eleme_restaurant",
          urlInfo = seed
        ))
    }

//    system.scheduler.schedule(0.seconds, 10.millis, injectActor, Tick)
//    system.scheduler.scheduleOnce(1.seconds, injectActor, Tick)
//    system.scheduler.scheduleOnce(10.seconds, injectActor, Tick)
//    system.scheduler.scheduleOnce(10.seconds, injectActor, Tick)
//    system.scheduler.scheduleOnce(10.seconds, injectActor, Tick)
//    system.scheduler.scheduleOnce(10.seconds, injectActor, Tick)

  }

}
