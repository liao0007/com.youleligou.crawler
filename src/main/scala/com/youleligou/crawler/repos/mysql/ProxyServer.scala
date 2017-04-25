package com.youleligou.crawler.repos.mysql

import java.sql.Timestamp

import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.daos.ProxyServerDao
import slick.jdbc.MySQLProfile.api._
import slick.lifted.Tag

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future
import scala.util.control.NonFatal

/**
  * Created by liangliao on 25/4/17.
  */
class ProxyServerRepo @Inject()(@Named(schemas.CanCan) database: Database) extends LazyLogging {
  val ProxyServersDao: TableQuery[ProxyServerTable] = TableQuery[ProxyServerTable]

  def find(id: Long): Future[Option[ProxyServerDao]] =
    database.run(ProxyServersDao.filter(_.id === id).result.headOption) recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        None
    }

  def delete(id: Long): Future[Int] =
    database.run(ProxyServersDao.filter(_.id === id).delete) recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        0
    }

  def all(limitOpt: Option[Int] = None): Future[List[ProxyServerDao]] =
    database.run {
      val prequery = ProxyServersDao.sortBy(_.checkCount.asc)
      limitOpt
        .fold(prequery) { limit =>
          prequery.take(limit)
        }
        .to[List]
        .result

    } recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        List.empty[ProxyServerDao]
    }

  def all(limit: Int): Future[List[ProxyServerDao]] =
    database.run(ProxyServersDao.to[List].sortBy(_.lastVerifiedAt.desc).take(limit).result) recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        List.empty[ProxyServerDao]
    }

  def create(proxyServerDao: ProxyServerDao): Future[Long] =
    database.run(ProxyServerDao returning ProxyServersDao.map(_.id) += proxyServerDao).recover {
      case t: Throwable =>
        logger.warn(t.getMessage)
        0L
    } recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        0L
    }

  def create(crawlerProxyServers: List[ProxyServerDao]): Future[Option[Int]] =
    database.run(ProxyServers Dao ++= crawlerProxyServers) recover {
      case NonFatal(x) if !x.getMessage.contains("Duplicate entry") =>
        logger.warn(x.getMessage)
        None
    }

  def insertOrUpdate(crawlerProxyServer: ProxyServerDao): Future[Int] =
    database.run {
      ProxyServersDao.insertOrUpdate(crawlerProxyServer)
    } recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        0
    }

  def insertOrUpdate(crawlerProxyServers: Seq[ProxyServerDao]): Future[Any] =
    database.run {
      DBIO.sequence(crawlerProxyServers.map(ProxyServersDao.insertOrUpdate))
    } recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
    }
}

class ProxyServerTable(tag: Tag) extends Table[ProxyServerDao](tag, "crawler_proxy_server") {
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
    (id, hash, ip, port, username.?, password.?, isAnonymous.?, supportedType.?, location.?, reactTime.?, isLive, lastVerifiedAt.?, checkCount) <> ((ProxyServerDao.apply _).tupled, ProxyServerDao.unapply)

}
