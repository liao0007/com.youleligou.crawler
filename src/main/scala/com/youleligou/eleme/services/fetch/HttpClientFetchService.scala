package com.youleligou.eleme.services.fetch

import com.google.inject.Inject
import com.typesafe.config.Config
import com.youleligou.core.reps.Repo
import com.youleligou.crawler.daos.JobDao
import play.api.libs.ws.ahc.StandaloneAhcWSClient

/**
  * Created by young.yang on 2016/8/28.
  * 采用HttpClient实现的爬取器
  */
class HttpClientFetchService @Inject()(config: Config, jobRepo: Repo[JobDao], standaloneAhcWSClient: StandaloneAhcWSClient)
    extends com.youleligou.crawler.services.fetch.HttpClientFetchService(config, jobRepo, standaloneAhcWSClient)
