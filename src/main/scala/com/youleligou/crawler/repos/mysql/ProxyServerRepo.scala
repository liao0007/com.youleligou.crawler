package com.youleligou.crawler.repos.mysql

import java.sql.Timestamp

import com.google.inject.Inject
import com.youleligou.core.reps.MysqlRepo
import com.youleligou.crawler.daos.ProxyServerDao
import org.joda.time.DateTime
import slick.jdbc.MySQLProfile.api._
import com.github.tototoshi.slick.MySQLJodaSupport._
import slick.lifted.Tag

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future
import scala.util.control.NonFatal
import scala.concurrent.ExecutionContext.Implicits.global
/**
  * Created by liangliao on 25/4/17.
  */
class ProxyServerRepo @Inject()(val schema: String = "cancan", val table: String = "proxy_server", val database: Database)
    extends MysqlRepo[ProxyServerDao] {
  val ProxyServerDaos: TableQuery[ProxyServerTable] = TableQuery[ProxyServerTable]

  def find(ip: String): Future[Option[ProxyServerDao]] =
    database.run(ProxyServerDaos.filter(_.ip === ip).result.headOption) recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        None
    }

  def delete(ip: String): Future[Int] =
    database.run(ProxyServerDaos.filter(_.ip === ip).delete) recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        0
    }

  def all(limitOpt: Option[Int] = None): Future[List[ProxyServerDao]] =
    database.run {
      val prequery = ProxyServerDaos.sortBy(_.checkCount.asc)
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

  def all(): Future[Seq[ProxyServerDao]] =
    database.run(ProxyServerDaos.to[Seq].result) recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        List.empty[ProxyServerDao]
    }

  def save(proxyServerDao: ProxyServerDao): Future[Any] =
    database.run(ProxyServerDaos += proxyServerDao) recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
    }

  def save(proxyServerDaos: Seq[ProxyServerDao]): Future[Option[Int]] =
    database.run(ProxyServerDaos ++= proxyServerDaos) recover {
      case NonFatal(x) if !x.getMessage.contains("Duplicate entry") =>
        logger.warn(x.getMessage)
        None
    }

  def insertOrUpdate(proxyServer: ProxyServerDao): Future[Int] =
    database.run {
      ProxyServerDaos.insertOrUpdate(proxyServer)
    } recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        0
    }

  def insertOrUpdate(proxyServers: Seq[ProxyServerDao]): Future[Any] =
    database.run {
      DBIO.sequence(proxyServers.map(ProxyServerDaos.insertOrUpdate))
    } recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
    }
}

class ProxyServerTable(tag: Tag) extends Table[ProxyServerDao](tag, "proxy_server") {

  def ip = column[String]("ip", O.PrimaryKey)

  def port = column[Int]("port")

  def username = column[String]("username")

  def password = column[String]("password")

  def isAnonymous = column[Boolean]("is_anonymous")

  def supportedType = column[String]("supported_type")

  def location = column[String]("location")

  def reactTime = column[Float]("react_time")

  def isLive = column[Boolean]("is_live")

  def lastVerifiedAt = column[DateTime]("last_verified_at")

  def checkCount = column[Int]("check_count")

  def createdAt = column[DateTime]("created_at")

  def * =
    (ip, port, username.?, password.?, isAnonymous.?, supportedType.?, location.?, reactTime.?, isLive, lastVerifiedAt.?, checkCount, createdAt) <> ((ProxyServerDao.apply _).tupled, ProxyServerDao.unapply)

}
