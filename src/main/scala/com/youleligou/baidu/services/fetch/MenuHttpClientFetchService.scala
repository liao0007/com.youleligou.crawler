package com.youleligou.baidu.services.fetch

import com.google.inject.Inject
import com.typesafe.config.Config
import com.youleligou.core.reps.Repo
import com.youleligou.crawler.daos.JobDaoSearch
import com.youleligou.crawler.models.{FetchRequest, FetchResponse, UrlInfo}
import com.youleligou.crawler.services.fetch.HttpClientFetchService
import play.api.libs.ws.ahc.StandaloneAhcWSClient
import play.api.libs.ws.{StandaloneWSRequest, StandaloneWSResponse}

import scala.concurrent.{ExecutionContext, Future}

class MenuHttpClientFetchService @Inject()(config: Config, jobSearchRepo: Repo[JobDaoSearch], standaloneAhcWSClient: StandaloneAhcWSClient)
    extends HttpClientFetchService(config, jobSearchRepo, standaloneAhcWSClient) {

  override def fetch(fetchRequest: FetchRequest)(implicit executor: ExecutionContext): Future[FetchResponse] = {
    val urlInfo: UrlInfo = fetchRequest.urlInfo
    val shopId: String   = urlInfo.queryParameters("shop_id")

    val request: StandaloneWSRequest = buildRequest(
      urlInfo.url,
      Seq(
        "Host"             -> "waimai.baidu.com",
        "Accept"           -> "application/json",
        "Referer"          -> s"http://waimai.baidu.com/mobile/waimai?qt=shopmenu&is_attr=1&shop_id=$shopId",
        "X-Requested-With" -> "XMLHttpRequest",
        "Origin"           -> "http://waimai.baidu.com"
      )
    )
    val response: Future[StandaloneWSResponse] = makeRequest(request) { r =>
      r.get()
    }
    processResponse(fetchRequest, response)
  }

}
