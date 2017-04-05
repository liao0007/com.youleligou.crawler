package com.youleligou.crawler.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Stash}
import com.typesafe.config.Config
import com.youleligou.crawler.actors.AbstractFetchActor.{Fetch, SetCrawlerProxyServers}
import com.youleligou.crawler.actors.CountActor._
import com.youleligou.crawler.actors.ProxyAssistantActor.Get
import com.youleligou.crawler.daos.CrawlerProxyServer
import com.youleligou.crawler.models.UrlInfo
import com.youleligou.crawler.services.FetchService
import com.youleligou.crawler.services.FetchService.FetchException

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits._
import scala.util.{Failure, Success}

/**
  * Created by young.yang on 2016/8/28.
  * 网页抓取任务,采用Actor实现
  */
abstract class AbstractFetchActor(config: Config,
                                  fetchService: FetchService,
                                  parserActor: ActorRef,
                                  countActor: ActorRef,
                                  proxyAssistantActor: ActorRef)
  extends Actor
    with Stash
    with ActorLogging {

  val proxyServers: mutable.Seq[CrawlerProxyServer] = mutable.Seq.empty[CrawlerProxyServer]

  var retry = 1
  val maxRetry = 20

  override def preStart(): Unit = {
    super.preStart()
    if (proxyServers.nonEmpty) {
      context.become(proxyServerAvailable)
    } else {
      context.become(proxyServerUnavailable)
    }
  }

  override def receive: Receive = proxyServerUnavailable

  def proxyServerUnavailable: Receive = {
    case _ =>
      log.info("proxy server list unavailable, getting")
      stash()
      proxyAssistantActor ! Get
      context.become(gettingProxyServer)
  }

  def gettingProxyServer: Receive = {
    case SetCrawlerProxyServers(pendingCrawlerProxyServers) =>
      log.info("server list got")
      this.proxyServers ++: pendingCrawlerProxyServers
      unstashAll()
      context.become(proxyServerAvailable)
    case _ =>
      log.info("getting proxy server list")
      stash()
  }

  def proxyServerAvailable: Receive = {
    case Fetch(jobName, urlInfo) if proxyServers.nonEmpty =>
      log.info("fetch: " + urlInfo)
      countActor ! FetchCounter(1)

      fetchService.fetch(jobName, urlInfo, proxyServers.head) onComplete {
        case Success(fetchResult) =>
          retry = 1
          log.info("fetch success: " + urlInfo.url)
          parserActor ! fetchResult
          countActor ! FetchOk(1)

        case Failure(FetchException(statusCode, message)) if retry < maxRetry =>
          retry = retry + 1
          statusCode match {
            case FetchService.PaymentRequired =>
              log.error("fetch failed: " + statusCode + " " + message + ", system terminating")
              context.system.terminate()

            case FetchService.NotFound =>
              log.error("fetch failed: " + statusCode + " " + message + " fetch aborted")

            case _ if retry < maxRetry =>
              retry = retry + 1
              log.info("fetch failed: " + statusCode + " " + message + " re-fetch")
              self ! urlInfo
          }
          countActor ! FetchError(1)

        case _ =>
          retry = 1
          log.info("fetch failed with retry limit: " + urlInfo.url)
      }

    case message if proxyServers.isEmpty =>
      self ! message
      context.become(proxyServerUnavailable)
  }
}

object AbstractFetchActor {

  case class SetCrawlerProxyServers(crawlerProxyServers: Seq[CrawlerProxyServer])
  case class Fetch(jobName: String, urlInfo: UrlInfo)
}
