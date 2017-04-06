package com.youleligou.eleme.services

import com.google.inject.Inject
import com.youleligou.crawler.actors.AbstractFetchActor.Fetch
import com.youleligou.crawler.actors.AbstractInjectActor.SeedInitialized
import com.youleligou.crawler.daos.CrawlerJob.FetchJobType
import com.youleligou.crawler.daos.CrawlerJobRepo
import com.youleligou.crawler.models.UrlInfo
import com.youleligou.crawler.models.UrlInfo.GenerateType
import com.youleligou.crawler.services.InjectService

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

class RestaurantInjectService @Inject()(crawlerJobRepo: CrawlerJobRepo) extends InjectService {

  override def initSeed(): Future[SeedInitialized] = {
    val idPattern = """[0-9]+""".r
    crawlerJobRepo.findWithMaxId(FetchJobType, RestaurantInjectService.fetchJobName) map {
      case Some(crawlerJob) => idPattern.findFirstIn(crawlerJob.url).map(_.toInt).getOrElse(0)
      case None => 0
    } map SeedInitialized
  }

  override def generateFetch(seed: Int): Fetch = {
    Fetch(
      RestaurantInjectService.fetchJobName,
      UrlInfo(
        s"http://mainsite-restapi.ele.me/shopping/restaurant/$seed",
        Set[(String, String)](
          //        "extras[]" -> "activity",
          //        "extras[]" -> "license",
          "extras[]" -> "identification",
          //        "extras[]" -> "albums",
          "extras[]" -> "flavors"
        ),
        GenerateType,
        0
      )
    )
  }
}

object RestaurantInjectService {
  final val name = "RestaurantInjectService"

  val fetchJobName = "fetch_eleme_restaurant"
}