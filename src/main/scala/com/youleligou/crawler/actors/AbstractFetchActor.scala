package com.youleligou.crawler.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Stash}
import com.typesafe.config.Config
import com.youleligou.crawler.actors.AbstractFetchActor._
import com.youleligou.crawler.actors.ProxyAssistantActor.{Init => _, _}
import com.youleligou.crawler.daos.CrawlerProxyServer
import com.youleligou.crawler.models.UrlInfo
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

  final val maxRetry = 20

  override def receive: Receive = standby

  def standby: Receive = {
    case Init =>
      proxyAssistantActor ! GetProxyServer

    case ProxyServer(server) =>
      unstashAll()
      context become proxyServerAvailable(server)

    case ProxyServerUnavailable =>
      unstashAll()
      context become standby

    case _ =>
      stash()
  }

  def proxyServerAvailable(proxyServer: CrawlerProxyServer): Receive = {
    case FetchUrl(jobName, urlInfo) =>
      fetchService.fetch(jobName, urlInfo, proxyServer) pipeTo self
      unstashAll()
      context become fetching(proxyServer, 1)

    case _ =>
      stash()
  }

  def fetching(proxyServer: CrawlerProxyServer, retry: Int): Receive = {
    case fetchResult @ FetchResult(FetchService.Ok, _, _, urlInfo) =>
      parserActor ! fetchResult
      unstashAll()
      context.become(proxyServerUnavailable)

    case FetchResult(statusCode @ FetchService.NotFound, _, message, _) =>
      log.warning("fetch failed: " + statusCode + " " + message)
      unstashAll()
      context.become(proxyServerUnavailable)

    case FetchResult(statusCode @ FetchService.PaymentRequired, _, message, _) =>
      log.warning("fetch failed: " + statusCode + " " + message + ", system terminating")
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
      unstashAll()
      context.become(proxyServerUnavailable)

    case _ =>
      stash()
  }
}

object AbstractFetchActor extends NamedActor {
  override final val name     = "FetchActor"
  override final val poolName = "FetchActor"

  sealed trait Event
  object Init                                            extends Event
  case class FetchUrl(jobName: String, urlInfo: UrlInfo) extends Event

  sealed trait Data
  case class FetchResult(status: Int, content: String, message: String, urlInfo: UrlInfo) extends Data {
    override def toString: String = "status=" + status + ",context length=" + content.length + ",url=" + urlInfo
  }

}
