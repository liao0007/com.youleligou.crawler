package com.youleligou.crawler.services.fetch

import java.sql.Timestamp
import java.time.LocalDateTime

import com.google.inject.Inject
import com.typesafe.config.Config
import com.youleligou.core.reps.{ElasticSearchRepo, Repo}
import com.youleligou.crawler.daos.JobDaoSearch
import com.youleligou.crawler.models.{FetchRequest, FetchResponse, Job}
import com.youleligou.crawler.services.FetchService
import play.api.libs.ws.ahc.StandaloneAhcWSClient
import play.api.libs.ws.{DefaultWSProxyServer, StandaloneWSRequest, StandaloneWSResponse}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source
import scala.util.Random
import scala.util.control.NonFatal

/**
  * Created by young.yang on 2016/8/28.
  * 采用HttpClient实现的爬取器
  */
class HttpClientFetchService @Inject()(config: Config, jobSearchRepo: Repo[JobDaoSearch], standaloneAhcWSClient: StandaloneAhcWSClient)
    extends FetchService {
  val rand = new Random(System.currentTimeMillis())

  import com.github.andr83.scalaconfig._
  val useProxy: Boolean                = config.getBoolean("crawler.fetch.useProxy")
  val proxyType                        = config.getString("crawler.fetch.proxy")
  val proxyConfig: Map[String, String] = config.as[Map[String, String]](proxyType)
  val userAgents: Seq[String]          = config.as[Seq[String]]("crawler.fetch.userAgents")
  val userAgentsSize: Int              = userAgents.length
  val timeout: Duration                = Duration(config.getInt("crawler.fetch.timeout"), MILLISECONDS)

  def fetch(fetchRequest: FetchRequest)(implicit executor: ExecutionContext): Future[FetchResponse] = {
    val request: StandaloneWSRequest = buildRequest(fetchRequest.urlInfo.url)
    val response: Future[StandaloneWSResponse] = makeRequest(request) { r =>
      r.get()
    }
    processResponse(fetchRequest, response)
  }

  def buildRequest(url: String, headers: Seq[(String, String)] = Seq.empty[(String, String)]): StandaloneWSRequest = {
    val cascadedHeader = headers ++ Seq(
      "User-Agent"                -> userAgents(rand.nextInt(userAgentsSize)),
      "Upgrade-Insecure-Requests" -> "1",
      "Accept-Language"           -> "zh-CN",
      "Accept-Encoding"           -> "gzip, deflate",
      "Connection"                -> "keep-alive",
      "Cache-Control"             -> "max-age=0"
    )

    val clientWithUrl =
      standaloneAhcWSClient
        .url(url)
        .withHeaders(cascadedHeader: _*)
        .withRequestTimeout(timeout)

    val withForwardedFor = addXForwardedFor(clientWithUrl)

    val clientWithProxy =
      if (useProxy) {

        val proxyServer = if (proxyType == "proxy.data5u") {
          val proxyInfo = Source.fromURL(proxyConfig("url")).mkString.trim.split(":")
          Map(
            "host"     -> proxyInfo(0),
            "port"     -> proxyInfo(1),
            "username" -> "",
            "password" -> ""
          )
        } else {
          proxyConfig
        }

        withForwardedFor
          .withProxyServer(
            DefaultWSProxyServer(host = proxyServer("host"),
                                 port = proxyServer("port").toInt,
                                 principal = Some(proxyServer("username")),
                                 password = Some(proxyServer("password"))))

      } else
        withForwardedFor

    clientWithProxy
  }

  def makeRequest(request: StandaloneWSRequest)(function: StandaloneWSRequest => Future[StandaloneWSResponse]): Future[StandaloneWSResponse] =
    try {
//      Thread.sleep((Math.abs(Math.sin(rand.nextDouble())) * 1000).toInt)
      function(request)
    } catch {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        Future.failed(x)
    }

  def processResponse(fetchRequest: FetchRequest, futureResponse: Future[StandaloneWSResponse])(
      implicit executor: ExecutionContext): Future[FetchResponse] = {
    val FetchRequest(urlInfo, _) = fetchRequest
    val crawlerJob = Job(
      url = urlInfo.toString,
      jobName = urlInfo.jobType,
      useProxy = useProxy
    )

    futureResponse
      .map { response =>
        jobSearchRepo.save(
          crawlerJob.copy(statusCode = Some(response.status),
                          statusMessage = Some(response.statusText),
                          completedAt = Some(Timestamp.valueOf(LocalDateTime.now()))))
        FetchResponse(response.status, response.body, response.statusText, fetchRequest)
      } recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        jobSearchRepo.save(crawlerJob.copy(statusCode = Some(999), statusMessage = Some(x.getMessage)))
        x.getMessage match {
          case "Remotely closed" =>
            FetchResponse(FetchService.RemoteClosed, "", x.getMessage, fetchRequest)
          case _ =>
            FetchResponse(FetchService.Timeout, "", x.getMessage, fetchRequest)
        }
    }

  }

  def addXForwardedFor(standaloneWSRequest: StandaloneWSRequest): StandaloneWSRequest = {
    val roll = Random.nextInt(100)
    if (roll < 70) {
      standaloneWSRequest.withHeaders("X-Forwarded-For" -> randomIp)
    } else if (roll < 90) {
      standaloneWSRequest.withHeaders("X-Forwarded-For" -> Seq.fill(2)(randomIp).mkString(", "))
    } else {
      standaloneWSRequest
    }
  }

  def randomIp: String = Seq.fill(4)(Random.nextInt(255)).mkString(".")

}
