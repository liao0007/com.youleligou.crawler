package com.youleligou.crawler.service.filter

import com.google.inject.Inject
import com.typesafe.config.Config
import com.youleligou.crawler.model.UrlInfo

/**
  * Created by liangliao on 1/4/17.
  */
class DefaultFilterService @Inject()(config: Config) extends FilterService {
  private val fetchDeep = config.getInt("crawler.actor.fetch.deep")

  override def filter(urlInfo: UrlInfo): Boolean = {
    urlInfo.deep < fetchDeep && urlInfo.url.startsWith(urlInfo.domain) && urlInfo.url.startsWith("http")
  }
}
