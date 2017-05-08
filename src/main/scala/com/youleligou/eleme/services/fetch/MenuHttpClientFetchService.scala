package com.youleligou.eleme.services.fetch

import com.google.inject.Inject
import com.typesafe.config.Config
import com.youleligou.core.reps.Repo
import com.youleligou.crawler.daos.JobDao
import com.youleligou.crawler.models.{FetchRequest, FetchResponse, UrlInfo}
import com.youleligou.crawler.services.FetchService
import org.joda.time.DateTime
import play.api.libs.json.{JsString, Json}
import play.api.libs.ws.{StandaloneWSRequest, StandaloneWSResponse}
import play.api.libs.ws.ahc.StandaloneAhcWSClient

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by young.yang on 2016/8/28.
  * 采用HttpClient实现的爬取器
  */
class MenuHttpClientFetchService @Inject()(config: Config, jobRepo: Repo[JobDao], standaloneAhcWSClient: StandaloneAhcWSClient)
    extends com.youleligou.crawler.services.fetch.HttpClientFetchService(config, jobRepo, standaloneAhcWSClient) {

  override def fetch(fetchRequest: FetchRequest)(implicit executor: ExecutionContext): Future[FetchResponse] = {

    val request: StandaloneWSRequest = buildRequest(
      fetchRequest.urlInfo.url,
      Seq(
        "Host"                      -> "mainsite-restapi.ele.me",
        "Accept"                    -> "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
        "Upgrade-Insecure-Requests" -> "1",
        "Accept-Language"           -> "zh-CN",
        "Accept-Encoding"           -> "gzip, deflate",
        "Connection"                -> "keep-alive"
      )
    )

    val response: Future[StandaloneWSResponse] = makeRequest(request) { r =>
      r.get()
    }
    processResponse(fetchRequest, response)

  }
}
