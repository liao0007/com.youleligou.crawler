package com.youleligou

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.actors.Injector
import com.youleligou.crawler.actors.Injector.{CacheCleared, ClearCache, Tick}
import com.youleligou.crawler.models.{FetchRequest, UrlInfo}
import com.youleligou.eleme.repos.cassandra.RestaurantSnapshotRepo
import redis.RedisClient

import scala.concurrent.duration._

/**
  * Created by dell on 2016/8/29.
  * 爬虫主函数
  */
class ElemeCrawlerBootstrap @Inject()(config: Config,
                                      system: ActorSystem,
                                      redisClient: RedisClient,
                                      restaurantRepo: RestaurantSnapshotRepo,
                                      @Named(Injector.PoolName) injectors: ActorRef)
    extends LazyLogging {

  import system.dispatcher

  val restaurantListConfig = config.getConfig("crawler.eleme.job.restaurantList")
  val foodListConfig       = config.getConfig("crawler.eleme.job.foodList")

  /**
    * 爬虫启动函数
    */
  def startRestaurant(): Unit = {
    val restaurantListJobType = restaurantListConfig.getString("jobType")
    implicit val timeout      = Timeout(10.minutes)
    injectors ? ClearCache(restaurantListJobType) map {
      case CacheCleared(_) =>
        import com.github.andr83.scalaconfig._
        val seeds = restaurantListConfig.as[Seq[UrlInfo]]("seed")
        seeds.foreach { seed =>
          injectors ! Injector.Inject(FetchRequest(
                                        urlInfo = seed
                                      ),
                                      force = true)
        }

        system.scheduler.schedule(60.seconds,
                                  FiniteDuration(restaurantListConfig.getInt("interval"), MILLISECONDS),
                                  injectors,
                                  Tick(restaurantListJobType))
      case _ => logger.warn("restraunt injector cache clear failed")
    }
  }

  def startFood(): Unit = {
    val foodListJobType = foodListConfig.getString("jobType")

    implicit val timeout = Timeout(10.minutes)
    injectors ? ClearCache(foodListJobType) map {
      case CacheCleared(_) =>
        restaurantRepo.allIds() foreach { id =>
          injectors ! Injector.Inject(
            FetchRequest(
              urlInfo = UrlInfo(
                domain = s"http://mainsite-restapi.ele.me/shopping/v2/menu?restaurant_id=$id",
                jobType = foodListJobType,
                services = Map(
                  "ParseService" -> "com.youleligou.eleme.services.food.ParseService"
                )
              )
            ),
            force = true
          )

        }
        system.scheduler.schedule(60.seconds, FiniteDuration(foodListConfig.getInt("interval"), MILLISECONDS), injectors, Tick(foodListJobType))
      case _ => logger.warn("food injector cache clear failed")
    }

  }
}
