package com.youleligou.crawler.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Stash}
import com.typesafe.config.Config
import com.youleligou.crawler.actors.AbstractFetchActor.Fetch
import com.youleligou.crawler.actors.ProxyAssistantActor.{CachedProxyServer, GetProxyServer}
import com.youleligou.crawler.daos.CrawlerProxyServer
import com.youleligou.crawler.models.{FetchResult, UrlInfo}
import com.youleligou.crawler.services.FetchService
import akka.pattern.pipe

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

  import context.dispatcher

  var proxyServer: CrawlerProxyServer = _
  var retry                           = 1
  val maxRetry                        = 20

  override def receive: Receive = proxyServerUnavailable

  def proxyServerUnavailable: Receive = {
    case _ =>
      log.info("proxy server unavailable, getting")
      stash()
      proxyAssistantActor ! GetProxyServer
      unstashAll()
      context.become(gettingProxyServer)

  }

  def gettingProxyServer: Receive = {
    case CachedProxyServer(Some(pendingProxyServer)) =>
      log.info("proxy server got")
      proxyServer = pendingProxyServer
      unstashAll()
      context.become(proxyServerAvailable)

    case CachedProxyServer(None) =>
      log.info("proxy server got None, retry")
      unstashAll()
      context.become(proxyServerUnavailable)

    case _ =>
      log.info("still getting proxy server")
      stash()
  }

  def proxyServerAvailable: Receive = {
    case Fetch(jobName, urlInfo) =>
      log.info("fetch: " + urlInfo)
      fetchService.fetch(jobName, urlInfo, proxyServer) pipeTo self
      unstashAll()
      context.become(fetching)

    case _ =>
      log.info("waiting for fetch job")
      stash()

  }

  def fetching: Receive = {
    case fetchResult @ FetchResult(FetchService.Ok, _, _, urlInfo) =>
      log.info("fetch success: " + urlInfo.url)
      retry = 1
      parserActor ! fetchResult
      unstashAll()
      context.become(proxyServerUnavailable)

    case FetchResult(statusCode @ FetchService.NotFound, _, message, _) =>
      log.warning("fetch failed: " + statusCode + " " + message)
      retry = 1
      unstashAll()
      context.become(proxyServerUnavailable)

    case FetchResult(statusCode @ FetchService.PaymentRequired, _, message, _) =>
      log.warning("fetch failed: " + statusCode + " " + message + ", system terminating")
      retry = 1
      unstashAll()
      context.system.terminate()

    case FetchResult(statusCode @ _, _, message, _) if retry < maxRetry =>
      log.warning("fetch failed: " + statusCode + " " + message, " retrying")
      retry = retry + 1
      stash()
      unstashAll()
      context.become(proxyServerUnavailable)

    case FetchResult(statusCode @ _, _, message, _) if retry >= maxRetry =>
      log.warning("fetch failed: " + statusCode + " " + message, " retry limit hit, giving up")
      retry = 1
      unstashAll()
      context.become(proxyServerUnavailable)

    case _ =>
      stash()
  }
}

object AbstractFetchActor {

  sealed trait FetchActorCommand
  case class Fetch(jobName: String, urlInfo: UrlInfo) extends FetchActorCommand
}
