package com.youleligou.crawler.daos.cassandra.crawler

import com.google.inject.Inject
import com.google.inject.name.Named
import com.outworkers.phantom.connectors.CassandraConnection
import com.outworkers.phantom.dsl._
import com.youleligou.crawler.daos.cassandra.keyspaces

/**
  * Created by liangliao on 18/4/17.
  */
class CrawlerDatabase @Inject()(@Named(keyspaces.Crawler) cassandraConnection: CassandraConnection)
    extends Database[CrawlerDatabase](cassandraConnection) {
  object crawlerJobs extends CrawlerJobs with Connector
}