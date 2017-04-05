package com.youleligou.crawler.services.fetch

import com.google.inject.Inject
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.daos.{CrawlerJob, CrawlerJobRepo}
import com.youleligou.crawler.models.{FetchResult, UrlInfo}
import com.youleligou.crawler.services.FetchService
import com.youleligou.crawler.services.FetchService.FetchException
import play.api.libs.ws.ahc.StandaloneAhcWSClient

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

/**
  * Created by young.yang on 2016/8/28.
  * 采用HttpClient实现的爬取器
  */
class HttpClientFetchService @Inject()(config: Config, standaloneAhcWSClient: StandaloneAhcWSClient, crawlerJobRepo: CrawlerJobRepo)
  extends FetchService
    with LazyLogging {
  def fetch(jobName: String, urlInfo: UrlInfo): Future[FetchResult] = {
    val start = System.currentTimeMillis()
    standaloneAhcWSClient
      .url(urlInfo.url)
      .withHeaders("User-Agent" -> config.getString("crawler.actor.fetch.userAgent"))
      //      .withAuth(config.getString("proxy.user"), config.getString("proxy.password"), WSAuthScheme.BASIC)
      //      .withProxyServer(DefaultWSProxyServer(host = config.getString("proxy.host"), port = config.getInt("proxy.port")))
      .get()
      .map { response =>
        logger.info("fetching " + urlInfo + ", cost time: " + (System.currentTimeMillis() - start) + " content length: " + response.body.length)
        crawlerJobRepo.create(
          CrawlerJob(
            url = urlInfo.url,
            jobName = jobName,
            statusCode = Some(response.status),
            statusMessage = Some(response.statusText)
          ))
        if (response.status == FetchService.Ok) {
          FetchResult(response.status, response.body, response.statusText, urlInfo)
        } else {
          throw FetchException(response.status, response.statusText + " " + urlInfo)
        }
      }

  }
}
