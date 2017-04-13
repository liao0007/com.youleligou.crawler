package com.youleligou.crawler.services.fetch

import com.google.inject.Inject
import com.typesafe.config.Config
import com.youleligou.crawler.daos.{CrawlerJob, CrawlerJobRepo, CrawlerProxyServer}
import com.youleligou.crawler.models.{FetchRequest, FetchResponse}
import com.youleligou.crawler.services.FetchService
import play.api.libs.ws.ahc.StandaloneAhcWSClient
import play.api.libs.ws.{DefaultWSProxyServer, WSAuthScheme}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Random, Try}

/**
  * Created by young.yang on 2016/8/28.
  * 采用HttpClient实现的爬取器
  */
class HttpClientFetchService @Inject()(config: Config, standaloneAhcWSClient: StandaloneAhcWSClient, crawlerJobRepo: CrawlerJobRepo)
    extends FetchService {

  import com.github.andr83.scalaconfig._
  val userAgents     = config.as[Seq[String]]("crawler.fetch.userAgents")
  val userAgentsSize = userAgents.length

  val timeout = Duration(config.getInt("crawler.fetch.timeout"), MILLISECONDS)

  def fetch(fetchRequest: FetchRequest, crawlerProxyServerOpt: Option[CrawlerProxyServer])(
      implicit executor: ExecutionContext): Future[FetchResponse] = {

    val start                                     = System.currentTimeMillis()
    val FetchRequest(requestName, urlInfo, retry) = fetchRequest
    val rand                                      = new Random(System.currentTimeMillis())

    Try {
      val client = standaloneAhcWSClient
        .url(urlInfo.url)
        .withHeaders("User-Agent" -> userAgents(rand.nextInt(userAgentsSize)))
        .withRequestTimeout(timeout)

      val proxyClient =
        if (crawlerProxyServerOpt.isDefined && crawlerProxyServerOpt.get.username.nonEmpty) {
          val crawlerProxyServer = crawlerProxyServerOpt.get
          client
            .withAuth(crawlerProxyServer.username.get, crawlerProxyServer.password.get, WSAuthScheme.BASIC)
            .withProxyServer(DefaultWSProxyServer(host = crawlerProxyServer.ip, port = crawlerProxyServer.port))
        } else if (crawlerProxyServerOpt.isDefined && crawlerProxyServerOpt.get.username.isEmpty) {
          val crawlerProxyServer = crawlerProxyServerOpt.get
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
              proxy = crawlerProxyServerOpt.map(crawlerProxyServer => s"""${crawlerProxyServer.ip}:${crawlerProxyServer.port}"""),
              statusCode = Some(response.status),
              statusMessage = Some(response.statusText)
            )
          )
          FetchResponse(response.status, response.body, response.statusText, fetchRequest)
        }
    } getOrElse {
      crawlerJobRepo.create(
        CrawlerJob(
          url = urlInfo.domain,
          jobName = requestName,
          proxy = crawlerProxyServerOpt.map(crawlerProxyServer => s"""${crawlerProxyServer.ip}:${crawlerProxyServer.port}"""),
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
