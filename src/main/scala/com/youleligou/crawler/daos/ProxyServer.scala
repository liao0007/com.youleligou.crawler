package com.youleligou.crawler.daos

import java.sql.Timestamp

import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.daos.schema.CanCan
import slick.jdbc.MySQLProfile.api._
import slick.lifted.Tag
import slick.sql.SqlProfile.ColumnOption.SqlType

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

case class CrawlerProxyServer(
                               id: Long = 0,
                               hash: String,
                               ip: String,
                               port: Int,
                               isAnonymous: Option[Boolean],
                               supportedType: Option[String],
                               location: Option[String],
                               reactTime: Option[Float],
                               isLive: Boolean = false,
                               lastVerifiedAt: Option[Timestamp],
                               createdAt: Timestamp = new Timestamp(System.currentTimeMillis())
                             )

class CrawlerProxyServerRepo extends LazyLogging {
  val CrawlerProxyServers: TableQuery[CrawlerProxyServerTable] = TableQuery[CrawlerProxyServerTable]

  def find(id: Long): Future[Option[CrawlerProxyServer]] =
    CanCan.db.run(CrawlerProxyServers.filter(_.id === id).result.headOption)

  def delete(id: Long): Future[Int] =
    CanCan.db.run(CrawlerProxyServers.filter(_.id === id).delete)

  def all(): Future[List[CrawlerProxyServer]] =
    CanCan.db.run(CrawlerProxyServers.to[List].result)

  def create(crawlerProxyServer: CrawlerProxyServer): Future[Long] =
    CanCan.db.run(CrawlerProxyServers returning CrawlerProxyServers.map(_.id) += crawlerProxyServer).recover {
      case t: Throwable =>
        logger.error(t.getMessage)
        0l
    }

  def create(crawlerProxyServers: List[CrawlerProxyServer]): Future[Option[Int]] =
    CanCan.db.run(CrawlerProxyServers ++= crawlerProxyServers)
}

class CrawlerProxyServerTable(tag: Tag) extends Table[CrawlerProxyServer](tag, "crawler_proxy_server") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def hash = column[String]("hash")

  def ip = column[String]("ip")

  def port = column[Int]("port")

  def isAnonymous = column[Boolean]("is_anonymous")

  def supportedType = column[String]("supported_type")

  def location = column[String]("location")

  def reactTime = column[Float]("react_time")

  def isLive = column[Boolean]("is_live")

  def lastVerifiedAt = column[Timestamp]("last_verified_at")

  def createdAt = column[Timestamp]("created_at", SqlType("timestamp not null default CURRENT_TIMESTAMP"))

  def * =
    (id, hash, ip, port, isAnonymous.?, supportedType.?, location.?, reactTime.?, isLive, lastVerifiedAt.?, createdAt) <> ((CrawlerProxyServer.apply _).tupled, CrawlerProxyServer.unapply)

}
