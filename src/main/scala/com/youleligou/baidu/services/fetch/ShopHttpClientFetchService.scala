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

class ShopHttpClientFetchService @Inject()(config: Config, jobSearchRepo: Repo[JobDaoSearch], standaloneAhcWSClient: StandaloneAhcWSClient)
    extends HttpClientFetchService(config, jobSearchRepo, standaloneAhcWSClient) {

  override def fetch(fetchRequest: FetchRequest)(implicit executor: ExecutionContext): Future[FetchResponse] = {
    val urlInfo: UrlInfo = fetchRequest.urlInfo
    val latitude: Float  = urlInfo.queryParameters("lat").toFloat
    val longitude: Float = urlInfo.queryParameters("lng").toFloat

    val request: StandaloneWSRequest = buildRequest(
      urlInfo.url,
      Seq(
        "Host"             -> "waimai.baidu.com",
        "Accept"           -> "application/json",
        "X-Requested-With" -> "XMLHttpRequest",
        "Referer"          -> s"http://waimai.baidu.com/mobile/waimai?qt=shoplist&lat=$latitude&lng=$longitude",
        "Origin"           -> "http://waimai.baidu.com"
      )
    )
    val response: Future[StandaloneWSResponse] = makeRequest(request) { r =>
      r.get()
    }
    processResponse(fetchRequest, response)

  }

}
