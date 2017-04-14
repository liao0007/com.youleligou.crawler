package com.youleligou.eleme

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.actors.AbstractInjectActor
import com.youleligou.crawler.actors.AbstractInjectActor.{CacheCleared, ClearCache, Tick}
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

  val elemeConfig = config.getConfig("crawler.job.eleme")

  /**
    * 爬虫启动函数
    */
  def startRestaurant(): Unit = {
    import com.github.andr83.scalaconfig._

    val config = elemeConfig.getConfig("restaurant-list")
    val seeds  = config.as[Seq[UrlInfo]]("seed")
    seeds.foreach { seed =>
      restaurantInjectorPool ! AbstractInjectActor.Inject(FetchRequest(
                                                            requestName = "fetch_eleme_restaurant",
                                                            urlInfo = seed
                                                          ),
                                                          force = true)
    }
    system.scheduler.schedule(5.seconds, FiniteDuration(config.getInt("interval"), MILLISECONDS), restaurantInjectorPool, Tick)
  }

  def startFood(): Unit = {
    val config = elemeConfig.getConfig("food-list")

    implicit val timeout = Timeout(5.minutes)
    foodInjectorPool ? ClearCache map {
      case CacheCleared(_) =>
        foodInjectorPool ! AbstractInjectActor.Inject(
          FetchRequest(
            requestName = "fetch_eleme_food",
            urlInfo = UrlInfo(
              domain = s"http://mainsite-restapi.ele.me/shopping/v2/menu?restaurant_id=41990"
            )
          ),
          force = true
        )

        /*
        restaurantRepo.allIds() map { ids =>
          ids.foreach { id =>
            foodInjectorPool ! AbstractInjectActor.Inject(FetchRequest(
                                                            requestName = "fetch_eleme_food",
                                                            urlInfo = UrlInfo(
                                                              domain = s"http://mainsite-restapi.ele.me/shopping/v2/menu?restaurant_id=$id"
                                                            )
                                                          ),
                                                          force = true)
          }
        }
         */

        system.scheduler.schedule(5.seconds, FiniteDuration(config.getInt("interval"), MILLISECONDS), foodInjectorPool, Tick)

      case _ => logger.warn("food injector cache clear failed")
    }

  }
}
