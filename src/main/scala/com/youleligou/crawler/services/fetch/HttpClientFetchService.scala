package com.youleligou.crawler.services.fetch

import com.google.inject.Inject
import com.typesafe.config.Config
import com.youleligou.crawler.daos.{CrawlerJob, CrawlerJobRepo}
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
  val useProxy: Boolean                = config.getBoolean("crawler.fetch.useProxy")
  val proxyServer: Map[String, String] = config.as[Map[String, String]](config.getString("crawler.fetch.proxy"))
  val userAgents: Seq[String]          = config.as[Seq[String]]("crawler.fetch.userAgents")
  val userAgentsSize: Int              = userAgents.length
  val timeout: Duration                = Duration(config.getInt("crawler.fetch.timeout"), MILLISECONDS)

  def fetch(fetchRequest: FetchRequest)(implicit executor: ExecutionContext): Future[FetchResponse] = {

    val FetchRequest(requestName, urlInfo, _) = fetchRequest
    val rand                                  = new Random(System.currentTimeMillis())

    Try {
      val client = standaloneAhcWSClient
        .url(urlInfo.url)
        .withHeaders("User-Agent" -> userAgents(rand.nextInt(userAgentsSize)))
        .withRequestTimeout(timeout)

      val proxyClient =
        if (useProxy) {
          client
            .withAuth(proxyServer("username"), proxyServer("password"), WSAuthScheme.BASIC)
            .withProxyServer(DefaultWSProxyServer(host = proxyServer("host"), port = proxyServer("port").toInt))
        } else
          client

      proxyClient
        .get()
        .map { response =>
          crawlerJobRepo.create(
            CrawlerJob(
              url = urlInfo.url,
              jobName = requestName,
              useProxy = useProxy,
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
          useProxy = useProxy,
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
