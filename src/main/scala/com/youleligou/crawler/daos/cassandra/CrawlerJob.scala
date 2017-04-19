package com.youleligou.crawler.daos.cassandra

import java.util.UUID

import com.outworkers.phantom.dsl._
import com.youleligou.crawler.daos.cassandra.CrawlerJob.JobType
import org.joda.time.DateTime

import scala.concurrent.Future

/**
  * Created by liangliao on 18/4/17.
  */
case class CrawlerJob(
    id: UUID = UUID.randomUUID(),
    jobType: String = JobType.Fetch.toString,
    jobName: String,
    url: String,
    useProxy: Boolean = false,
    statusCode: Option[Int] = None,
    statusMessage: Option[String] = None,
    createdAt: DateTime = DateTime.now(),
    completedAt: Option[DateTime] = None
)

object CrawlerJob {
  object JobType extends Enumeration {
    val Fetch = Value
  }
}

abstract class CrawlerJobs extends CassandraTable[CrawlerJobs, CrawlerJob] with RootConnector {
  object jobType       extends StringColumn(this) with PartitionKey
  object jobName       extends StringColumn(this) with PartitionKey
  object id            extends UUIDColumn(this)
  object url           extends StringColumn(this)
  object useProxy      extends BooleanColumn(this)
  object statusCode    extends OptionalIntColumn(this)
  object statusMessage extends OptionalStringColumn(this)
  object completedAt   extends OptionalDateTimeColumn(this)
  object createdAt     extends DateTimeColumn(this) with ClusteringOrder with Descending

  def batchInsertOrUpdate(crawlerJobs: Seq[CrawlerJob]): Future[ResultSet] =
    Batch.unlogged
      .add(crawlerJobs.map { crawlerJob =>
        store(crawlerJob)
      }.iterator)
      .future()

  def insertOrUpdate(crawlerJobs: Seq[CrawlerJob]): Seq[Future[ResultSet]] = crawlerJobs.map(insertOrUpdate)

  def insertOrUpdate(crawlerJob: CrawlerJob): Future[ResultSet] = store(crawlerJob).future()

  def all: Future[List[CrawlerJob]] = select.fetch()
}
