package com.youleligou.crawler.daos

import java.sql.Timestamp
import java.text.SimpleDateFormat

import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.daos.schema.CanCan
import play.api.libs.json._
import slick.jdbc.MySQLProfile.api._
import slick.lifted.Tag

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future
import scala.util.control.NonFatal

case class CrawlerProxyServer(
    id: Long = 0,
    hash: String,
    ip: String,
    port: Int,
    username: Option[String] = None,
    password: Option[String] = None,
    isAnonymous: Option[Boolean] = None,
    supportedType: Option[String] = None,
    location: Option[String] = None,
    reactTime: Option[Float] = None,
    isLive: Boolean = false,
    lastVerifiedAt: Option[Timestamp] = None,
    checkCount: Int = 0
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

class CrawlerProxyServerRepo @Inject()(@Named(CanCan) database: Database) extends LazyLogging {
  val CrawlerProxyServers: TableQuery[CrawlerProxyServerTable] = TableQuery[CrawlerProxyServerTable]

  def find(id: Long): Future[Option[CrawlerProxyServer]] =
    database.run(CrawlerProxyServers.filter(_.id === id).result.headOption) recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        None
    }

  def delete(id: Long): Future[Int] =
    database.run(CrawlerProxyServers.filter(_.id === id).delete) recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        0
    }

  def all(): Future[List[CrawlerProxyServer]] =
    database.run(CrawlerProxyServers.to[List].result) recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        List.empty[CrawlerProxyServer]
    }

  def all(limit: Int): Future[List[CrawlerProxyServer]] =
    database.run(CrawlerProxyServers.to[List].sortBy(_.lastVerifiedAt.desc).take(limit).result) recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        List.empty[CrawlerProxyServer]
    }

  def create(crawlerProxyServer: CrawlerProxyServer): Future[Long] =
    database.run(CrawlerProxyServers returning CrawlerProxyServers.map(_.id) += crawlerProxyServer).recover {
      case t: Throwable =>
        logger.warn(t.getMessage)
        0L
    } recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        0L
    }

  def create(crawlerProxyServers: List[CrawlerProxyServer]): Future[Option[Int]] =
    database.run(CrawlerProxyServers ++= crawlerProxyServers) recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        None
    }

  def insertOrUpdate(crawlerProxyServer: CrawlerProxyServer): Future[Int] =
    database.run {
      CrawlerProxyServers.insertOrUpdate(crawlerProxyServer)
    } recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        0
    }

  def insertOrUpdate(crawlerProxyServers: Seq[CrawlerProxyServer]): Future[Any] =
    database.run {
      DBIO.sequence(crawlerProxyServers.map(CrawlerProxyServers.insertOrUpdate))
    } recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
    }
}

class CrawlerProxyServerTable(tag: Tag) extends Table[CrawlerProxyServer](tag, "crawler_proxy_server") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def hash = column[String]("hash")

  def ip = column[String]("ip")

  def port = column[Int]("port")

  def username = column[String]("username")

  def password = column[String]("password")

  def isAnonymous = column[Boolean]("is_anonymous")

  def supportedType = column[String]("supported_type")

  def location = column[String]("location")

  def reactTime = column[Float]("react_time")

  def isLive = column[Boolean]("is_live")

  def lastVerifiedAt = column[Timestamp]("last_verified_at")

  def checkCount = column[Int]("check_count")

  def createdAt = column[Timestamp]("created_at")

  def * =
    (id, hash, ip, port, username.?, password.?, isAnonymous.?, supportedType.?, location.?, reactTime.?, isLive, lastVerifiedAt.?, checkCount) <> ((CrawlerProxyServer.apply _).tupled, CrawlerProxyServer.unapply)

}
