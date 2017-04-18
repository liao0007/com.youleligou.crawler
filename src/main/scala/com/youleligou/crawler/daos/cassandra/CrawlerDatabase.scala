package com.youleligou.crawler.daos.cassandra

import com.google.inject.Inject
import com.google.inject.name.Named
import com.outworkers.phantom.connectors.CassandraConnection
import com.outworkers.phantom.dsl._

/**
  * Created by liangliao on 18/4/17.
  */
class CrawlerDatabase @Inject()(@Named(keyspaces.Crawler) cassandraConnection: CassandraConnection)
    extends Database[CrawlerDatabase](cassandraConnection) {
  object crawlerJobs         extends CrawlerJobs with Connector
  object crawlerProxyServers extends CrawlerProxyServers with Connector
}
