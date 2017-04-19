package com.youleligou.migration

import akka.actor.ActorSystem
import com.google.inject.Inject
import com.outworkers.phantom.dsl.DatabaseProvider
import com.typesafe.config.Config
import com.youleligou.crawler.daos.cassandra.{CrawlerDatabase, CrawlerProxyServer}
import com.youleligou.crawler.daos.mysql.{CrawlerJobRepo, CrawlerProxyServerRepo}
import org.joda.time.DateTime

/**
  * Created by liangliao on 18/4/17.
  */
class CrawlerCassandraBootstrap @Inject()(config: Config,
                                          system: ActorSystem,
                                          val database: CrawlerDatabase,
                                          crawlerJobRepo: CrawlerJobRepo,
                                          crawlerProxyServerRepo: CrawlerProxyServerRepo)
    extends DatabaseProvider[CrawlerDatabase] {
  import system.dispatcher
  def start(): Unit = {

    for {
      _            <- database.createAsync()
      proxyServers <- crawlerProxyServerRepo.all()

    } yield {
      proxyServers foreach { proxyServer =>
        database.crawlerProxyServers
          .store(CrawlerProxyServer(
            ip = proxyServer.ip,
            port = proxyServer.port,
            username = proxyServer.username,
            password = proxyServer.password,
            isAnonymous = proxyServer.isAnonymous,
            supportedType = proxyServer.supportedType,
            location = proxyServer.location,
            reactTime = proxyServer.reactTime,
            isLive = proxyServer.isLive,
            lastVerifiedAt = proxyServer.lastVerifiedAt.map(new DateTime(_)),
            checkCount = proxyServer.checkCount,
            createdAt = DateTime.now()
          ))
          .future()
      }

    }
  }
  /*
  def start(): Unit = {

    for {
      _           <- database.createAsync()
      crawlerJobs <- crawlerJobRepo.all()

    } yield {
      crawlerJobs foreach { crawlerJob =>
        database.crawlerJobs
          .store(CrawlerJob(
            jobName = crawlerJob.jobName,
            url = crawlerJob.url,
            useProxy = crawlerJob.useProxy,
            createdAt = new DateTime(crawlerJob.createdAt),
            completedAt = Some(new DateTime(crawlerJob.createdAt)),
            statusCode = crawlerJob.statusCode,
            statusMessage = crawlerJob.statusMessage
          ))
          .future()
      }

    }
  }
 */
}
