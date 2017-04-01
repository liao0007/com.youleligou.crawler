package com.youleligou.crawler.service.fetch

import com.google.inject.Inject
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.model.UrlInfo.GenerateType
import com.youleligou.crawler.model.{FetchResult, UrlInfo}
import com.youleligou.crawler.service.cache.CacheService
import com.youleligou.crawler.service.fetch.FetchService.FetchException
import com.youleligou.crawler.service.hash.HashService
import play.api.libs.ws.ahc.StandaloneAhcWSClient

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

/**
  * Created by young.yang on 2016/8/28.
  * 采用HttpClient实现的爬取器
  */
class HttpClientFetchService @Inject()(config: Config,
                                       standaloneAhcWSClient: StandaloneAhcWSClient,
                                       cacheService: CacheService,
                                       hashService: HashService)
  extends FetchService
    with LazyLogging {
  def fetch(urlInfo: UrlInfo): Future[Option[FetchResult]] = {
    val md5 = hashService.hash(urlInfo.url)
    cacheService.get(md5) flatMap {
      case None =>
        cacheService.put(md5, "1") flatMap {
          case true =>
            val start = System.currentTimeMillis()
            standaloneAhcWSClient
              .url(urlInfo.url)
              .withHeaders("User-Agent" -> config.getString("crawler.fetch.userAgent"))
              .get()
              .map { response =>
                logger.info(
                  "fetch url " + urlInfo + ", cost time -" + (System.currentTimeMillis() - start) + " content length -" + response.body.length)
                if (response.status == FetchService.Ok) {
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
