package com.youleligou.eleme.services.fetch

import com.google.inject.Inject
import com.typesafe.config.Config
import com.youleligou.core.reps.Repo
import com.youleligou.crawler.daos.JobDao
import com.youleligou.crawler.models.{FetchRequest, FetchResponse, UrlInfo}
import play.api.libs.ws.ahc.StandaloneAhcWSClient
import play.api.libs.ws.{StandaloneWSRequest, StandaloneWSResponse}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by young.yang on 2016/8/28.
  * 采用HttpClient实现的爬取器
  */
class MenuHttpClientFetchService @Inject()(config: Config, jobRepo: Repo[JobDao], standaloneAhcWSClient: StandaloneAhcWSClient)
    extends com.youleligou.crawler.services.fetch.HttpClientFetchService(config, jobRepo, standaloneAhcWSClient) {

  override def fetch(fetchRequest: FetchRequest)(implicit executor: ExecutionContext): Future[FetchResponse] = {

    val urlInfo: UrlInfo = fetchRequest.urlInfo
    val restaurantId     = urlInfo.queryParameters("restaurant_id")

    val request: StandaloneWSRequest = buildRequest(
      fetchRequest.urlInfo.url,
      Seq(
        "Host"    -> "www.ele.me",
        "Accept"  -> "application/json, text/plain, */*",
        "Referer" -> s"https://www.ele.me/shop/$restaurantId"
      )
    )

    val response: Future[StandaloneWSResponse] = makeRequest(request) { r =>
      r.get()
    }
    processResponse(fetchRequest, response)

  }
}
