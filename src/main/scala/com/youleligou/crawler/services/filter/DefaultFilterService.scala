package com.youleligou.crawler.services.filter

import com.google.inject.Inject
import com.typesafe.config.Config
import com.youleligou.crawler.models.UrlInfo
import com.youleligou.crawler.services.FilterService

/**
  * Created by liangliao on 1/4/17.
  */
class DefaultFilterService @Inject()(config: Config) extends FilterService {
  private val fetchDeep = config.getInt("crawler.actor.fetch.deep")

  override def filter(urlInfo: UrlInfo): Boolean = {
    urlInfo.url.startsWith(urlInfo.domain) && urlInfo.url.startsWith("http") //&& urlInfo.deep < fetchDeep
  }
}
