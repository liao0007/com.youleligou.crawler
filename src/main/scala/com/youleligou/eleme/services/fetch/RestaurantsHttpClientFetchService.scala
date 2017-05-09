package com.youleligou.eleme.services.fetch

import com.google.inject.Inject
import com.typesafe.config.Config
import com.youleligou.core.reps.Repo
import com.youleligou.core.services.GeoHash
import com.youleligou.crawler.daos.JobDao
import com.youleligou.crawler.models.{FetchRequest, FetchResponse, UrlInfo}
import play.api.libs.ws.ahc.StandaloneAhcWSClient
import play.api.libs.ws.{StandaloneWSRequest, StandaloneWSResponse}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by young.yang on 2016/8/28.
  * 采用HttpClient实现的爬取器
  */
class RestaurantsHttpClientFetchService @Inject()(config: Config,
                                                  jobRepo: Repo[JobDao],
                                                  standaloneAhcWSClient: StandaloneAhcWSClient,
                                                  geoHash: GeoHash)
    extends com.youleligou.crawler.services.fetch.HttpClientFetchService(config, jobRepo, standaloneAhcWSClient) {

  override def fetch(fetchRequest: FetchRequest)(implicit executor: ExecutionContext): Future[FetchResponse] = {
    val urlInfo: UrlInfo = fetchRequest.urlInfo

    val latitude    = urlInfo.queryParameters("latitude")
    val longitude   = urlInfo.queryParameters("longitude")
    val geoHashCode = geoHash.encode(latitude.toDouble, longitude.toDouble)

    val request: StandaloneWSRequest = buildRequest(
      urlInfo.copy(queryParameters = urlInfo.queryParameters ++ Map("geohash" -> geoHashCode)).url,
      Seq(
        "Host"                      -> "www.ele.me",
        "Accept"                    -> "application/json, text/plain, */*",
        "x-shard"                   -> s"loc=$longitude,$latitude",
        "Referer"                   -> s"https://www.ele.me/place/$geoHashCode?latitude=$latitude&longitude=$longitude",
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
