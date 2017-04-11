package com.youleligou.crawler.daos

import java.sql.Timestamp
import java.text.SimpleDateFormat

import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.daos.schema.CanCan
import play.api.libs.json._
import slick.jdbc.MySQLProfile.api._
import slick.lifted.Tag

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

object CrawlerProxyServer {

  implicit object timestampFormat extends Format[Timestamp] {
    val format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS'Z'")

    def reads(json: JsValue) = {
      val str = json.as[String]
      JsSuccess(new Timestamp(format.parse(str).getTime))
    }

    def writes(ts: Timestamp) = JsString(format.format(ts))
  }

  implicit val jsonFormat: OFormat[CrawlerProxyServer] = Json.format[CrawlerProxyServer]
}

class CrawlerProxyServerRepo extends LazyLogging {
  val CrawlerProxyServers: TableQuery[CrawlerProxyServerTable] = TableQuery[CrawlerProxyServerTable]

  def find(id: Long): Future[Option[CrawlerProxyServer]] =
    CanCan.db.run(CrawlerProxyServers.filter(_.id === id).result.headOption) recover {
      case x: Throwable =>
        logger.warn(x.getMessage)
        None
    }

  def delete(id: Long): Future[Int] =
    CanCan.db.run(CrawlerProxyServers.filter(_.id === id).delete) recover {
      case x: Throwable =>
        logger.warn(x.getMessage)
        0
    }

  def all(): Future[List[CrawlerProxyServer]] =
    CanCan.db.run(CrawlerProxyServers.to[List].result) recover {
      case x: Throwable =>
        logger.warn(x.getMessage)
        List.empty[CrawlerProxyServer]
    }

  def all(limit: Int): Future[List[CrawlerProxyServer]] =
    CanCan.db.run(CrawlerProxyServers.to[List].sortBy(_.lastVerifiedAt.desc).take(limit).result) recover {
      case x: Throwable =>
        logger.warn(x.getMessage)
        List.empty[CrawlerProxyServer]
    }

  def create(crawlerProxyServer: CrawlerProxyServer): Future[Long] =
    CanCan.db.run(CrawlerProxyServers returning CrawlerProxyServers.map(_.id) += crawlerProxyServer).recover {
      case t: Throwable =>
        logger.warn(t.getMessage)
        0L
    } recover {
      case x: Throwable =>
        logger.warn(x.getMessage)
        0L
    }

  def create(crawlerProxyServers: List[CrawlerProxyServer]): Future[Option[Int]] =
    CanCan.db.run(CrawlerProxyServers ++= crawlerProxyServers) recover {
      case x: Throwable =>
        logger.warn(x.getMessage)
        None
    }

  def insertOrUpdate(crawlerProxyServer: CrawlerProxyServer): Future[Int] =
    CanCan.db.run {
      CrawlerProxyServers.insertOrUpdate(crawlerProxyServer)
    } recover {
      case x: Throwable =>
        logger.warn(x.getMessage)
        0
    }
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

  def createdAt = column[Timestamp]("created_at")

  def * =
    (id, hash, ip, port, isAnonymous.?, supportedType.?, location.?, reactTime.?, isLive, lastVerifiedAt.?, createdAt) <> ((CrawlerProxyServer.apply _).tupled, CrawlerProxyServer.unapply)

}
