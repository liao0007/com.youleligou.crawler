package com.youleligou.crawler.service.fetch

import com.google.inject.Inject
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.model.{FetchResult, UrlInfo}
import com.youleligou.crawler.service.fetch.FetchService.FetchException
import play.api.libs.ws.DefaultWSProxyServer
import play.api.libs.ws.ahc.StandaloneAhcWSClient

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

/**
  * Created by young.yang on 2016/8/28.
  * 采用HttpClient实现的爬取器
  */
class HttpClientFetchService @Inject()(config: Config, standaloneAhcWSClient: StandaloneAhcWSClient) extends FetchService with LazyLogging {
  def fetch(urlInfo: UrlInfo): Future[FetchResult] = {
    val start = System.currentTimeMillis()
    standaloneAhcWSClient
      .url(urlInfo.url)
      .withHeaders("User-Agent" -> config.getString("crawler.actor.fetch.userAgent"))
      //      .withProxyServer(
      //        DefaultWSProxyServer(host = config.getString("crawler.actor.fetch.proxy.host"), port = config.getInt("crawler.actor.fetch.proxy.port")))
      .get()
      .map { response =>
        logger.debug("fetching " + urlInfo + ", cost time: " + (System.currentTimeMillis() - start) + " content length: " + response.body.length)
        if (response.status == FetchService.Ok) {
          FetchResult(response.status, response.body, response.statusText, urlInfo)
        } else {
          throw FetchException(response.status, response.statusText + " " + urlInfo)
        }
      }

  }
}
