package com.youleligou.crawler.services.fetch

import com.google.inject.Inject
import com.outworkers.phantom.database.DatabaseProvider
import com.typesafe.config.Config
import com.youleligou.crawler.daos.JobDao
import com.youleligou.crawler.daos.cassandra.CrawlerDatabase
import com.youleligou.crawler.models.{FetchRequest, FetchResponse}
import com.youleligou.crawler.repos.cassandra.JobRepo
import com.youleligou.crawler.services.FetchService
import org.joda.time.DateTime
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
class HttpClientFetchService @Inject()(config: Config, crawlerRep: JobRepo, standaloneAhcWSClient: StandaloneAhcWSClient)
    extends FetchService {

  import com.github.andr83.scalaconfig._
  val useProxy: Boolean                = config.getBoolean("crawler.fetch.useProxy")
  val proxyServer: Map[String, String] = config.as[Map[String, String]](config.getString("crawler.fetch.proxy"))
  val userAgents: Seq[String]          = config.as[Seq[String]]("crawler.fetch.userAgents")
  val userAgentsSize: Int              = userAgents.length
  val timeout: Duration                = Duration(config.getInt("crawler.fetch.timeout"), MILLISECONDS)

  def fetch(fetchRequest: FetchRequest)(implicit executor: ExecutionContext): Future[FetchResponse] = {
    val FetchRequest(urlInfo, _) = fetchRequest
    val rand                     = new Random(System.currentTimeMillis())

    val crawlerJob = JobDao(
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
          crawlerRep.save(crawlerJob.copy(statusCode = Some(response.status), statusMessage = Some(response.statusText), completedAt = Some(DateTime.now())))
//          database.crawlerJobs.insertOrUpdate(
//            crawlerJob.copy(statusCode = Some(response.status), statusMessage = Some(response.statusText), completedAt = Some(DateTime.now()))
//          )
          FetchResponse(response.status, response.body, response.statusText, fetchRequest)
        } recover {
        case NonFatal(x) =>
          logger.warn(x.getMessage)
          crawlerRep.save(crawlerJob.copy(statusCode = Some(999), statusMessage = Some(x.getMessage)))
//          database.crawlerJobs.insertOrUpdate(crawlerJob.copy(statusCode = Some(999), statusMessage = Some(x.getMessage)))
          x.getMessage match {
            case "Remotely closed" =>
              FetchResponse(FetchService.RemoteClosed, "", x.getMessage, fetchRequest)
            case _ =>
              FetchResponse(FetchService.Timeout, "", x.getMessage, fetchRequest)
          }
      }
    } catch {
      case NonFatal(x) =>
        crawlerRep.save(crawlerJob.copy(statusCode = Some(999), statusMessage = Some(x.getMessage)))
//        database.crawlerJobs.insertOrUpdate()
        Future.successful(FetchResponse(FetchService.RemoteClosed, "", x.getMessage, fetchRequest))
    }
  }

  def addXForwardedFor(standaloneWSRequest: StandaloneWSRequest): StandaloneWSRequest = {
    val roll = Random.nextInt(100)
    if (roll < 60) {
      standaloneWSRequest.withHeaders("X-Forwarded-For" -> randomIp)
    } else if (roll < 80) {
      standaloneWSRequest.withHeaders("X-Forwarded-For" -> Seq.fill(2)(randomIp).mkString(", "))
    } else {
      standaloneWSRequest
    }
  }

  def randomIp: String = Seq.fill(4)(Random.nextInt(255)).mkString(".")

}
