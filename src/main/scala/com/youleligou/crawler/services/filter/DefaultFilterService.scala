package com.youleligou.crawler.services.filter

import com.google.inject.Inject
import com.typesafe.config.Config
import com.youleligou.crawler.models.UrlInfo
import com.youleligou.crawler.services.FilterService

/**
  * Created by liangliao on 1/4/17.
  */
class DefaultFilterService @Inject()(config: Config) extends FilterService {
  private val deep = config.getInt("crawler.fetch.deep")

  override def filter(urlInfo: UrlInfo): Boolean = {
    urlInfo.host.startsWith(urlInfo.host) && urlInfo.host.startsWith("http") //&& urlInfo.deep < fetchDeep
  }
}
