package com.youleligou.eleme

import akka.actor._
import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.actors.AbstractInjectActor
import com.youleligou.crawler.actors.AbstractInjectActor.Tick
import com.youleligou.crawler.models.UrlInfo.UrlInfoType
import com.youleligou.crawler.models.{FetchRequest, UrlInfo}
import com.youleligou.eleme.daos.RestaurantRepo
import redis.RedisClient

import scala.concurrent.duration._

/**
  * Created by dell on 2016/8/29.
  * 爬虫主函数
  */
class ElemeCrawlerBootstrap @Inject()(config: Config,
                                      system: ActorSystem,
                                      restaurantRepo: RestaurantRepo,
                                      redisClient: RedisClient,
                                      @Named(RestaurantInjectActor.poolName) restaurantInjectorPool: ActorRef,
                                      @Named(FoodInjectActor.poolName) foodInjectorPool: ActorRef)
    extends LazyLogging {

  import system.dispatcher

  /**
    * 爬虫启动函数
    */
  def startRestaurant(): Unit = {
    import com.github.andr83.scalaconfig._
    val seeds = config.as[Seq[UrlInfo]]("crawler.seed.eleme.restaurant-list")
    seeds.foreach { seed =>
      restaurantInjectorPool ! AbstractInjectActor.Inject(FetchRequest(
                                                            requestName = "fetch_eleme_restaurant",
                                                            urlInfo = seed
                                                          ),
                                                          force = true)
    }
    system.scheduler.schedule(5.seconds, 20.millis, restaurantInjectorPool, Tick)
  }

  def startFood(): Unit = {
    //clean cache
    redisClient.del("ElemeFoodInjectorPendingInjectingUrlQueueKey", "ElemeFoodInjectorInjectedUrlHashKey").map { _ =>
      restaurantRepo.allIds() map { ids =>
        ids.foreach { id =>
          foodInjectorPool ! AbstractInjectActor.Inject(FetchRequest(
                                                          requestName = "fetch_eleme_food",
                                                          urlInfo = UrlInfo(
                                                            host = s"http://mainsite-restapi.ele.me/shopping/v2/menu?restaurant_id=$id"
                                                          )
                                                        ),
                                                        force = true)
        }
      }
      system.scheduler.schedule(5.seconds, 20.millis, foodInjectorPool, Tick)
    }
  }
}
