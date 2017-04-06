package com.youleligou.crawler.services.fetch

import com.google.inject.Inject
import com.typesafe.config.Config
import com.youleligou.crawler.daos.{CrawlerJob, CrawlerJobRepo, CrawlerProxyServer}
import com.youleligou.crawler.models.{FetchResult, UrlInfo}
import com.youleligou.crawler.services.FetchService
import play.api.libs.ws.ahc.StandaloneAhcWSClient
import play.api.libs.ws.DefaultWSProxyServer

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

/**
  * Created by young.yang on 2016/8/28.
  * 采用HttpClient实现的爬取器
  */
class HttpClientFetchService @Inject()(config: Config, standaloneAhcWSClient: StandaloneAhcWSClient, crawlerJobRepo: CrawlerJobRepo)
    extends FetchService {
  def fetch(jobName: String, urlInfo: UrlInfo, crawlerProxyServer: CrawlerProxyServer)(implicit executor: ExecutionContext): Future[FetchResult] = {
    val start = System.currentTimeMillis()
    Try {
      standaloneAhcWSClient
        .url(urlInfo.url)
        .withHeaders("User-Agent" -> config.getString("crawler.actor.fetch.userAgent"))
        .withRequestTimeout(Duration(2, SECONDS))
        //      .withAuth(config.getString("proxy.user"), config.getString("proxy.password"), WSAuthScheme.BASIC)
        //      .withProxyServer(DefaultWSProxyServer(host = config.getString("proxy.host"), port = config.getInt("proxy.port")))
        .withProxyServer(DefaultWSProxyServer(host = crawlerProxyServer.ip, port = crawlerProxyServer.port))
        .get()
        .map { response =>
          logger.info("fetching " + urlInfo + ", cost time: " + (System.currentTimeMillis() - start) + " content length: " + response.body.length)
          crawlerJobRepo.create(
            CrawlerJob(
              url = urlInfo.url,
              jobName = jobName,
              proxy = Some(s"""${crawlerProxyServer.ip}:${crawlerProxyServer.port}"""),
              statusCode = Some(response.status),
              statusMessage = Some(response.statusText)
            )
          )
          FetchResult(response.status, response.body, response.statusText, urlInfo)
        }
    } getOrElse {
      Future.successful(FetchResult(FetchService.Timeout, "", "", urlInfo))
    }
  } recover {
    case x: Throwable =>
      logger.warn(x.getMessage)
      FetchResult(FetchService.NotFound, "", "", urlInfo)
  }
}
