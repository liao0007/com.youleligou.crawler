package com.youleligou.meituan.services.fetch

import java.sql.Timestamp
import java.time.LocalDateTime

import com.google.inject.Inject
import com.typesafe.config.Config
import com.youleligou.core.reps.Repo
import com.youleligou.crawler.daos.JobDao
import com.youleligou.crawler.models.{FetchRequest, FetchResponse, Job}
import com.youleligou.crawler.services.FetchService
import com.youleligou.crawler.services.fetch.HttpClientFetchService
import play.api.libs.ws.ahc.StandaloneAhcWSClient
import play.api.libs.ws.{DefaultWSProxyServer, StandaloneWSRequest}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random
import scala.util.control.NonFatal

/**
  * Created by young.yang on 2016/8/28.
  * 采用HttpClient实现的爬取器
  */
class MeituanHttpClientFetchService @Inject()(config: Config, jobRepo: Repo[JobDao], standaloneAhcWSClient: StandaloneAhcWSClient)
    extends HttpClientFetchService(config, jobRepo, standaloneAhcWSClient) {

  override def fetch(fetchRequest: FetchRequest)(implicit executor: ExecutionContext): Future[FetchResponse] = {
    val FetchRequest(urlInfo, _) = fetchRequest
    val rand                     = new Random(System.currentTimeMillis())

    val crawlerJob = Job(
      url = urlInfo.url,
      jobName = urlInfo.jobType,
      useProxy = useProxy
    )

    val clientWithUrl =
      standaloneAhcWSClient
        .url(urlInfo.url)
        .withHeaders("User-Agent" -> userAgents(rand.nextInt(userAgentsSize)))
        .withRequestTimeout(timeout)

    val withForwardedFor = addXForwardedFor(clientWithUrl)

    val clientWithProxy =
      if (useProxy) {
        withForwardedFor
          .withProxyServer(
            DefaultWSProxyServer(host = proxyServer("host"),
                                 port = proxyServer("port").toInt,
                                 principal = Some(proxyServer("username")),
                                 password = Some(proxyServer("password"))))

      } else
        withForwardedFor

    try {
      clientWithProxy
        .get()
        .map { response =>
          jobRepo.save(
            crawlerJob.copy(statusCode = Some(response.status),
                            statusMessage = Some(response.statusText),
                            completedAt = Some(Timestamp.valueOf(LocalDateTime.now()))))
          FetchResponse(response.status, response.body, response.statusText, fetchRequest)
        } recover {
        case NonFatal(x) =>
          logger.warn(x.getMessage)
          jobRepo.save(crawlerJob.copy(statusCode = Some(999), statusMessage = Some(x.getMessage)))
          x.getMessage match {
            case "Remotely closed" =>
              FetchResponse(FetchService.RemoteClosed, "", x.getMessage, fetchRequest)
            case _ =>
              FetchResponse(FetchService.Timeout, "", x.getMessage, fetchRequest)
          }
      }
    } catch {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        jobRepo.save(crawlerJob.copy(statusCode = Some(999), statusMessage = Some(x.getMessage)))
        Future.successful(FetchResponse(FetchService.RemoteClosed, "", x.getMessage, fetchRequest))
    }
  }

}
