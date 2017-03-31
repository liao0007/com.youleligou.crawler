package com.youleligou.crawler.fetchers

import javax.inject.Inject

import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.fetchers.Fetcher.FetchException
import com.youleligou.crawler.models.UrlInfo.GenerateType
import com.youleligou.crawler.models.{FetchResult, UrlInfo}
import com.youleligou.crawler.modules.Hasher
import play.api.libs.ws.ahc.StandaloneAhcWSClient
import redis.RedisClient

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

/**
  * Created by young.yang on 2016/8/28.
  * 采用HttpClient实现的爬取器
  */
class HttpClientFetcher @Inject()(config: Config, standaloneAhcWSClient: StandaloneAhcWSClient, redisClient: RedisClient, hasher: Hasher)
  extends Fetcher
    with LazyLogging {
  def fetch(urlInfo: UrlInfo): Future[Option[FetchResult]] = {
    val md5 = hasher.hash(urlInfo.url)
    redisClient.get(md5) flatMap {
      case None =>
        redisClient.set(md5, 1) flatMap {
          case true =>
            val start = System.currentTimeMillis()
            standaloneAhcWSClient
              .url(urlInfo.url)
              .withHeaders("User-Agent" -> config.getString("crawler.fetch.userAgent"))
              .get()
              .map { response =>
                logger.info(
                  "fetch url " + urlInfo + ", cost time -" + (System.currentTimeMillis() - start) + " content length -" + response.body.length)
                if (response.status == Fetcher.Ok) {
                  Some(FetchResult(response.status, response.body, response.statusText, urlInfo.url, urlInfo.deep))
                } else {
                  throw new FetchException("fetch error code is -" + response.status + ",error url is " + urlInfo)
                }
              }
          case _ =>
            throw new FetchException("failed to set cache - error url is " + urlInfo)
        }
      case _ if urlInfo.urlType == GenerateType =>
        logger.info("url  -" + urlInfo + " is fetched ")
        Future.successful(None)
    }
  }
}
