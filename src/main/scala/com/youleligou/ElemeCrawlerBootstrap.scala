package com.youleligou

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import com.youleligou.core.reps.ElasticSearchRepo
import com.youleligou.crawler.actors.Injector
import com.youleligou.crawler.actors.Injector.{CacheCleared, ClearCache, Tick}
import com.youleligou.crawler.models.{FetchRequest, UrlInfo}
import com.youleligou.eleme.daos.RestaurantDao
import com.youleligou.eleme.models.{Identification, Restaurant}
import com.youleligou.eleme.repos.cassandra.RestaurantRepo
import redis.RedisClient

import scala.concurrent.duration._

/**
  * Created by dell on 2016/8/29.
  * 爬虫主函数
  */
class ElemeCrawlerBootstrap @Inject()(config: Config,
                                      system: ActorSystem,
                                      redisClient: RedisClient,
                                      restaurantRepo: RestaurantRepo,
                                      restaurantEsRepo: ElasticSearchRepo[Restaurant],
                                      @Named(Injector.PoolName) injectors: ActorRef)
    extends LazyLogging {

  import system.dispatcher

  val restaurantListConfig = config.getConfig("crawler.eleme.job.restaurantList")
  val foodListConfig       = config.getConfig("crawler.eleme.job.foodList")

  /**
    * 爬虫启动函数
    */
  def cleanRestaurant(): Unit = {
    val restaurantListJobType = restaurantListConfig.getString("jobType")
    implicit val timeout      = Timeout(10.minutes)
    injectors ? ClearCache(restaurantListJobType) map {
      case CacheCleared(_) =>
      case _               => logger.warn("restraunt injector cache clear failed")
    }
  }

  def startRestaurant(): Unit = {
    val restaurantListJobType = restaurantListConfig.getString("jobType")
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
  }

  def indexRestaurant(): Unit = {
    restaurantRepo.all() flatMap { restaurantDaos =>
      val restaurants = restaurantDaos map { restaurantDao =>
        Restaurant(
          id = restaurantDao.id,
          name = restaurantDao.name,
          address = restaurantDao.address,
          imagePath = restaurantDao.imagePath,
          latitude = restaurantDao.latitude,
          longitude = restaurantDao.longitude,
          identification = Some(Identification(restaurantDao.licensesNumber, restaurantDao.companyName))
        )
      }
      restaurantEsRepo.save(restaurants)
    }
  }

  def cleanFood(): Unit = {
    val foodListJobType = foodListConfig.getString("jobType")

    implicit val timeout = Timeout(10.minutes)
    injectors ? ClearCache(foodListJobType) map {
      case CacheCleared(_) =>
      case _               => logger.warn("food injector cache clear failed")
    }
  }

  def startFood(): Unit = {
    val foodListJobType = foodListConfig.getString("jobType")
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
  }

}
