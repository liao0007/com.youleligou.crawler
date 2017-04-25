package com.youleligou.crawler.repos.cassandra

import com.google.inject.Inject
import com.youleligou.core.reps.CassandraRepo
import com.youleligou.crawler.daos.ProxyServerDao
import org.apache.spark.SparkContext

import scala.concurrent.Future

/**
  * Created by liangliao on 25/4/17.
  */
class ProxyServerRepo @Inject()(val sparkContext: SparkContext) extends CassandraRepo[ProxyServerDao] {
  val keyspace: String                            = "crawler"
  val table: String                               = "proxy_server"
  override def all(): Future[Seq[ProxyServerDao]] = ???
}
