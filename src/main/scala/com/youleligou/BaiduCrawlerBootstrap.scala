package com.youleligou

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import com.youleligou.baidu.repos.cassandra.ShopRepo
import com.youleligou.baidu.services.fetch.MenuHttpClientFetchService
import com.youleligou.baidu.services.parse.MenuParseService
import com.youleligou.crawler.actors.Injector
import com.youleligou.crawler.actors.Injector.{CacheCleared, ClearCache, Tick}
import com.youleligou.crawler.models.{FetchRequest, UrlInfo}
import org.apache.spark.SparkContext
import redis.RedisClient

import scala.concurrent.duration._

/**
  * Created by dell on 2016/8/29.
  * 爬虫主函数
  */
class BaiduCrawlerBootstrap @Inject()(config: Config,
                                      system: ActorSystem,
                                      redisClient: RedisClient,
                                      shopRepo: ShopRepo,
                                      sparkContext: SparkContext,
                                      @Named(Injector.PoolName) injectors: ActorRef)
    extends LazyLogging {

  import system.dispatcher

  val boostrapDelay              = config.getInt("crawler.boostrapDelay")
  val restaurantsFilterJobConfig = config.getConfig("crawler.baidu.job.shop")
  val menuJobConfig              = config.getConfig("crawler.baidu.job.menu")

  /**
    * 爬虫启动函数
    */
  def cleanRestaurants(): Unit = {
    val restaurantsJobType = restaurantsFilterJobConfig.getString("jobType")
    implicit val timeout   = Timeout(10.minutes)
    injectors ? ClearCache(restaurantsJobType) map {
      case CacheCleared(_) =>
      case _               => logger.warn("shop injector cache clear failed")
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

    system.scheduler.schedule(
      FiniteDuration(boostrapDelay, MILLISECONDS),
      FiniteDuration(restaurantsFilterJobConfig.getInt("interval"), MILLISECONDS),
      injectors,
      Tick(restaurantsJobType)
    )
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
    shopRepo.allShopIds() foreach { id =>
      injectors ! Injector.Inject(
        FetchRequest(
          urlInfo = UrlInfo(
            domain = "http://waimai.baidu.com",
            path = s"/mobile/waimai?",
            bodyParameters = Map(
              "qt"      -> "shopmenu",
              "display" -> "json",
              "shop_id" -> id.toString
            ),
            jobType = menuJobType,
            services = Map(
              "ParseService" -> classOf[MenuParseService].getName,
              "FetchService" -> classOf[MenuHttpClientFetchService].getName
            )
          )
        ),
        force = true
      )

    }
    system.scheduler.schedule(FiniteDuration(boostrapDelay, MILLISECONDS),
                              FiniteDuration(menuJobConfig.getInt("interval"), MILLISECONDS),
                              injectors,
                              Tick(menuJobType))
  }

}
