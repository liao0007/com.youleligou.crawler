package com.youleligou.crawler.daos.cassandra.crawler

import java.util.UUID

import com.outworkers.phantom.dsl._
import com.youleligou.crawler.daos.cassandra.crawler.CrawlerJob.JobType

/**
  * Created by liangliao on 18/4/17.
  */
case class CrawlerJob(
    id: UUID = UUID.randomUUID(),
    jobType: String = JobType.Fetch.toString,
    jobName: String,
    url: String,
    useProxy: Boolean = false,
    createdAt: DateTime,
    completedAt: DateTime,
    statusCode: Option[Int] = None,
    statusMessage: Option[String] = None
)

object CrawlerJob {
  object JobType extends Enumeration {
    val Fetch = Value
  }
}

abstract class CrawlerJobs extends CassandraTable[CrawlerJobs, CrawlerJob] with RootConnector {
  object id            extends UUIDColumn(this) with PartitionKey
  object jobType       extends StringColumn(this)
  object jobName       extends StringColumn(this)
  object url           extends StringColumn(this)
  object useProxy      extends BooleanColumn(this)
  object createdAt     extends DateTimeColumn(this) with ClusteringOrder with Descending
  object completedAt   extends DateTimeColumn(this) with ClusteringOrder with Descending
  object statusCode    extends OptionalIntColumn(this)
  object statusMessage extends OptionalStringColumn(this)
}
