package com.youleligou.eleme.services.fetch

import java.sql.Timestamp
import java.time.LocalDateTime

import com.google.inject.Inject
import com.typesafe.config.Config
import com.youleligou.core.reps.Repo
import com.youleligou.crawler.daos.JobDao
import com.youleligou.crawler.models.{FetchRequest, FetchResponse, Job}
import com.youleligou.crawler.services.FetchService
import play.api.libs.ws.DefaultWSProxyServer
import play.api.libs.ws.ahc.StandaloneAhcWSClient

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random
import scala.util.control.NonFatal

/**
  * Created by young.yang on 2016/8/28.
  * 采用HttpClient实现的爬取器
  */
class HttpClientFetchService @Inject()(config: Config, jobRepo: Repo[JobDao], standaloneAhcWSClient: StandaloneAhcWSClient)
    extends com.youleligou.crawler.services.fetch.HttpClientFetchService(config, jobRepo, standaloneAhcWSClient)
