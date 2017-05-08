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
import com.youleligou.meituan.repos.cassandra.PoiRepo
import com.youleligou.meituan.services.fetch.PoiFoodHttpClientFetchService
import com.youleligou.meituan.services.parse.PoiFoodParseService
import org.apache.spark.SparkContext
import redis.RedisClient

import scala.concurrent.duration._

/**
  * Created by dell on 2016/8/29.
  * 爬虫主函数
  */
class MeituanCrawlerBootstrap @Inject()(config: Config,
                                        system: ActorSystem,
                                        redisClient: RedisClient,
                                        poiRepo: PoiRepo,
                                        sparkContext: SparkContext,
                                        @Named(Injector.PoolName) injectors: ActorRef)
    extends LazyLogging {

  import system.dispatcher

  val restaurantsFilterJobConfig = config.getConfig("crawler.meituan.job.poiFilter")
  val menuJobConfig              = config.getConfig("crawler.meituan.job.poiFood")

  /**
    * 爬虫启动函数
    */
  def cleanRestaurants(): Unit = {
    val restaurantsJobType = restaurantsFilterJobConfig.getString("jobType")
    implicit val timeout   = Timeout(10.minutes)
    injectors ? ClearCache(restaurantsJobType) map {
      case CacheCleared(_) =>
      case _               => logger.warn("poi injector cache clear failed")
    }
  }

  def startRestaurants(): Unit = {
    val restaurantsJobType = restaurantsFilterJobConfig.getString("jobType")
    import com.github.andr83.scalaconfig._
    val seeds = restaurantsFilterJobConfig.as[Seq[UrlInfo]]("seed")
    seeds.foreach { seed =>
      injectors ! Injector.Inject(FetchRequest(
                                    urlInfo = seed
                                  ),
                                  force = true)
    }

    system.scheduler.schedule(10.seconds,
                              FiniteDuration(restaurantsFilterJobConfig.getInt("interval"), MILLISECONDS),
                              injectors,
                              Tick(restaurantsJobType))
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
    poiRepo.allWmPoiViewIds() foreach { id =>
      injectors ! Injector.Inject(
        FetchRequest(
          urlInfo = UrlInfo(
            domain = "http://i.waimai.meituan.com",
            path = s"/ajax/v8/poi/food?",
            bodyParameters = Map(
              "wm_poi_id" -> id.toString
            ),
            jobType = menuJobType,
            services = Map(
              "ParseService" -> classOf[PoiFoodParseService].getName,
              "FetchService" -> classOf[PoiFoodHttpClientFetchService].getName
            )
          )
        ),
        force = true
      )

    }
    system.scheduler.schedule(60.seconds, FiniteDuration(menuJobConfig.getInt("interval"), MILLISECONDS), injectors, Tick(menuJobType))
  }

}
