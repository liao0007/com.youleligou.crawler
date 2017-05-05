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
import com.youleligou.eleme.daos.accumulate.search.RestaurantAccumulateSearch
import com.youleligou.eleme.models.Restaurant
import com.youleligou.eleme.repos.cassandra.RestaurantRepo
import org.apache.spark.SparkContext
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
                                      restaurantEsRepo: ElasticSearchRepo[RestaurantAccumulateSearch],
                                      sparkContext: SparkContext,
                                      @Named(Injector.PoolName) injectors: ActorRef)
    extends LazyLogging {

  import system.dispatcher

  val restaurantsJobConfig = config.getConfig("crawler.eleme.job.restaurants")
  val menuJobConfig        = config.getConfig("crawler.eleme.job.menu")

  /**
    * 爬虫启动函数
    */
  def cleanRestaurants(): Unit = {
    val restaurantsJobType = restaurantsJobConfig.getString("jobType")
    implicit val timeout   = Timeout(10.minutes)
    injectors ? ClearCache(restaurantsJobType) map {
      case CacheCleared(_) =>
      case _               => logger.warn("restraunt injector cache clear failed")
    }
  }

  def startRestaurants(): Unit = {
    val restaurantsJobType = restaurantsJobConfig.getString("jobType")
    import com.github.andr83.scalaconfig._
    val seeds = restaurantsJobConfig.as[Seq[UrlInfo]]("seed")
    seeds.foreach { seed =>
      injectors ! Injector.Inject(FetchRequest(
                                    urlInfo = seed
                                  ),
                                  force = true)
    }

    system.scheduler.schedule(60.seconds, FiniteDuration(restaurantsJobConfig.getInt("interval"), MILLISECONDS), injectors, Tick(restaurantsJobType))
  }

  def indexRestaurants(): Unit = {
    restaurantRepo.all() flatMap { restaurantDaos =>
      val restaurants: Seq[RestaurantAccumulateSearch]                = restaurantDaos
      val restaurantSearchDaos: Seq[RestaurantAccumulateSearch] = restaurants
      restaurantEsRepo.save(restaurantSearchDaos)
    }
  }

  def cleanMenu(): Unit = {
    val menuJobType = menuJobConfig.getString("jobType")

    implicit val timeout = Timeout(10.minutes)
    injectors ? ClearCache(menuJobType) map {
      case CacheCleared(_) =>
      case _               => logger.warn("food injector cache clear failed")
    }
  }

  def startMenu(): Unit = {
    val menuJobType = menuJobConfig.getString("jobType")
    restaurantRepo.allIds() foreach { id =>
      injectors ! Injector.Inject(
        FetchRequest(
          urlInfo = UrlInfo(
            domain = "http://mainsite-restapi.ele.me",
            path = s"/shopping/v2/menu?restaurant_id=$id",
            jobType = menuJobType,
            services = Map(
              "ParseService" -> "com.youleligou.eleme.services.menu.ParseService"
            )
          )
        ),
        force = true
      )

    }
    system.scheduler.schedule(60.seconds, FiniteDuration(menuJobConfig.getInt("interval"), MILLISECONDS), injectors, Tick(menuJobType))
  }

}
