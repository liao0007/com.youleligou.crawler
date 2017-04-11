package com.youleligou.crawler.services.fetch

import com.google.inject.Inject
import com.typesafe.config.Config
import com.youleligou.crawler.daos.{CrawlerJob, CrawlerJobRepo, CrawlerProxyServer}
import com.youleligou.crawler.models.{FetchRequest, FetchResponse}
import com.youleligou.crawler.services.FetchService
import play.api.libs.ws.{DefaultWSProxyServer, WSAuthScheme}
import play.api.libs.ws.ahc.StandaloneAhcWSClient

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

/**
  * Created by young.yang on 2016/8/28.
  * 采用HttpClient实现的爬取器
  */
class HttpClientFetchService @Inject()(config: Config, standaloneAhcWSClient: StandaloneAhcWSClient, crawlerJobRepo: CrawlerJobRepo)
    extends FetchService {

  val useProxy = config.getBoolean("crawler.fetch.useProxy")

  def fetch(fetchRequest: FetchRequest, crawlerProxyServer: CrawlerProxyServer)(implicit executor: ExecutionContext): Future[FetchResponse] = {
    val start                                     = System.currentTimeMillis()
    val FetchRequest(requestName, urlInfo, retry) = fetchRequest
    Try {
      val client = standaloneAhcWSClient
        .url(urlInfo.url)
        .withHeaders("User-Agent" -> config.getString("crawler.fetch.userAgent"))
        .withRequestTimeout(Duration(config.getInt("crawler.fetch.timeout"), MILLISECONDS))

      val proxyClient =
        if (useProxy && crawlerProxyServer.username.nonEmpty) {
          client
            .withAuth(crawlerProxyServer.username.get, crawlerProxyServer.password.get, WSAuthScheme.BASIC)
            .withProxyServer(DefaultWSProxyServer(host = crawlerProxyServer.ip, port = crawlerProxyServer.port))
        } else if (useProxy && crawlerProxyServer.username.isEmpty) {
          client
            .withProxyServer(DefaultWSProxyServer(host = crawlerProxyServer.ip, port = crawlerProxyServer.port))
        } else
          client

      proxyClient
        .get()
        .map { response =>
          crawlerJobRepo.create(
            CrawlerJob(
              url = urlInfo.url,
              jobName = requestName,
              proxy = Some(s"""${crawlerProxyServer.ip}:${crawlerProxyServer.port}"""),
              statusCode = Some(response.status),
              statusMessage = Some(response.statusText)
            )
          )
          FetchResponse(response.status, response.body, response.statusText, fetchRequest)
        }
    } getOrElse {
      crawlerJobRepo.create(
        CrawlerJob(
          url = urlInfo.host,
          jobName = requestName,
          proxy = Some(s"""${crawlerProxyServer.ip}:${crawlerProxyServer.port}"""),
          statusCode = Some(FetchService.Timeout),
          statusMessage = None
        )
      )
      Future.successful(FetchResponse(FetchService.Timeout, "", "", fetchRequest))
    }
  } recover {
    case x: Throwable =>
      logger.warn(x.getMessage)
      FetchResponse(FetchService.Timeout, "", "", fetchRequest)
  }
}
