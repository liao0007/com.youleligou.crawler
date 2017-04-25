package com.youleligou.crawler.repos.mysql

import java.util.UUID

import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.daos.JobDao
import org.joda.time.DateTime
import slick.jdbc.MySQLProfile.api._
import slick.lifted.Tag
import slick.sql.SqlProfile.ColumnOption.SqlType

import scala.concurrent.Future
import scala.util.control.NonFatal

/**
  * Created by liangliao on 25/4/17.
  */
class JobDaoRepo @Inject()(@Named("cancan") database: Database) extends LazyLogging {
  val JobDaos: TableQuery[JobDaoTable] = TableQuery[JobDaoTable]

  def find(id: Long): Future[Option[JobDao]] =
    database.run(JobDaos.filter(_.id === id).result.headOption) recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        None
    }

  def findWithMaxId(jobType: String, jobName: String): Future[Option[JobDao]] =
    database.run(
      JobDaos
        .filter(job => job.jobType === jobType && job.jobName === jobName)
        .sortBy(_.id.desc)
        .take(1)
        .result
        .headOption) recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        None
    }

  def delete(id: Long): Future[Int] =
    database.run(JobDaos.filter(_.id === id).delete) recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        0
    }

  def all(): Future[List[JobDao]] =
    database.run(JobDaos.to[List].sortBy(_.id.desc).result) recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        List.empty[JobDao]
    }

  def create(job: JobDao): Future[Long] =
    database.run(JobDaos returning JobDaos.map(_.id) += job).recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        0L
    } recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        0L
    }

  def create(jobs: List[JobDao]): Future[Option[Int]] =
    database.run(JobDaos ++= jobs) recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        None
    }
}

class JobDaoTable(tag: Tag) extends Table[JobDao](tag, "job") {
  def id = column[UUID]("id", O.PrimaryKey)

  def jobType = column[String]("job_type")

  def jobName = column[String]("job_name")

  def url = column[String]("url")

  def useProxy = column[Boolean]("use_proxy")

  def createdAt = column[DateTime]("created_at", SqlType("timestamp not null default CURRENT_TIMESTAMP"))

  def statusCode = column[Int]("status_code")

  def statusMessage = column[String]("status_message")

  def * =
    (id, jobType, jobName, url, useProxy, createdAt, statusCode.?, statusMessage.?) <> ((JobDao.apply _).tupled, JobDao.unapply)

}
