package com.youleligou.eleme

import com.google.inject.Inject
import com.youleligou.crawler.model.UrlInfo
import com.youleligou.crawler.model.UrlInfo.GenerateType
import com.youleligou.crawler.service.GenerateService
import dao.RestaurantRepo

class RestaurantGenerateService @Inject()(restaurantRepo: RestaurantRepo) extends GenerateService {
  override def generate(seed: String): UrlInfo = {
    UrlInfo(
      "http://mainsite-restapi.ele.me/shopping/restaurant/" + seed,
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
  }
}

object RestaurantGenerateService {
  final val name = "RestaurantGenerateService"
}
