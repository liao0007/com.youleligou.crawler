package com.youleligou.eleme
import com.google.inject.Inject
import com.youleligou.crawler.model.{FetchResult, ParseResult, UrlInfo}
import com.youleligou.crawler.service.ParseService
import dao.{Restaurant, RestaurantRepo}
import play.api.libs.json.Json

class RestaurantParseService @Inject()(restaurantRepo: RestaurantRepo) extends ParseService {

  private def persist(content: String) = {
    Json.parse(content).validate[Restaurant].map(restaurantRepo.create)
  }

  /**
    * 解析具体实现
    */
  override def parse(fetchResult: FetchResult): ParseResult = {
    persist(fetchResult.content)
    ParseResult(
      urlInfo = fetchResult.urlInfo,
      content = fetchResult.content,
      publishTime = System.currentTimeMillis(),
      updateTime = System.currentTimeMillis(),
      childLink = List.empty[UrlInfo]
    )
  }
}

object RestaurantParseService {
  final val name = "CanteenParseService"
}
