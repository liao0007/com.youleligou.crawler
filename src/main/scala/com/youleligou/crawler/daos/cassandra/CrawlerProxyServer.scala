package com.youleligou.crawler.daos.cassandra

import com.outworkers.phantom.batch.BatchQuery
import com.outworkers.phantom.builder.Unspecified
import com.outworkers.phantom.builder.query.InsertQuery.Default
import com.outworkers.phantom.dsl._
import org.joda.time.DateTime

import scala.concurrent.Future

case class CrawlerProxyServer(
    ip: String,
    port: Int,
    username: Option[String] = None,
    password: Option[String] = None,
    isAnonymous: Option[Boolean] = None,
    supportedType: Option[String] = None,
    location: Option[String] = None,
    reactTime: Option[Float] = None,
    isLive: Boolean = false,
    lastVerifiedAt: Option[DateTime] = None,
    checkCount: Int = 0,
    createdAt: DateTime = DateTime.now()
)

abstract class CrawlerProxyServers extends CassandraTable[CrawlerProxyServers, CrawlerProxyServer] with RootConnector {
  object ip             extends StringColumn(this) with PartitionKey
  object port           extends IntColumn(this) with PartitionKey
  object username       extends OptionalStringColumn(this)
  object password       extends OptionalStringColumn(this)
  object isAnonymous    extends OptionalBooleanColumn(this)
  object supportedType  extends OptionalStringColumn(this)
  object location       extends OptionalStringColumn(this)
  object reactTime      extends OptionalFloatColumn(this)
  object isLive         extends BooleanColumn(this)
  object lastVerifiedAt extends OptionalDateTimeColumn(this)
  object checkCount     extends IntColumn(this)
  object createdAt      extends DateTimeColumn(this)

  def batchInsertOrUpdate(crawlerProxyServers: Seq[CrawlerProxyServer]): Future[ResultSet] =
    Batch.unlogged
      .add(crawlerProxyServers.map { crawlerProxyServer =>
        store(crawlerProxyServer)
      }.iterator)
      .future()

  def insertOrUpdate(crawlerProxyServers: Seq[CrawlerProxyServer]): Seq[Future[ResultSet]] = crawlerProxyServers map insertOrUpdate

  def insertOrUpdate(crawlerProxyServer: CrawlerProxyServer): Future[ResultSet] = store(crawlerProxyServer).future()

  def all(limitOpt: Option[Int] = None): Future[List[CrawlerProxyServer]] = limitOpt.fold(select.fetch()) { limit =>
    select.limit(limit).fetch()
  }
}
