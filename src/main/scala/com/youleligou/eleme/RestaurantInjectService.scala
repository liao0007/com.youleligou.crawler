package com.youleligou.eleme

import com.google.inject.Inject
import com.youleligou.crawler.actor.FetchActor.Fetch
import com.youleligou.crawler.dao.CrawlerJob.FetchJobType
import com.youleligou.crawler.dao.CrawlerJobRepo
import com.youleligou.crawler.model.UrlInfo
import com.youleligou.crawler.model.UrlInfo.GenerateType
import com.youleligou.crawler.service.InjectService
import dao.RestaurantRepo

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._

class RestaurantInjectService @Inject()(restaurantRepo: RestaurantRepo, crawlerJobRepo: CrawlerJobRepo) extends InjectService {

  override def initSeed(): Future[Int] = {
    val idPattern = """[0-9]+""".r
    crawlerJobRepo.findWithMaxId(FetchJobType, RestaurantInjectService.fetchJobName) map {
      case Some(crawlerJob) => idPattern.findFirstIn(crawlerJob.url).map(_.toInt).getOrElse(0)
      case None => 0
    }
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
