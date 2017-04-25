package com.youleligou.crawler.repos.mysql

import java.util.UUID

import com.google.inject.Inject
import com.typesafe.scalalogging.LazyLogging
import com.youleligou.core.reps.MysqlRepo
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
class JobDaoRepo @Inject()(val schema: String = "cancan", val database: Database) extends MysqlRepo[JobDao, JobDaoTable] with LazyLogging {

  def find(id: Long): Future[Option[JobDao]] =
    database.run(table.filter(_.id === id).result.headOption) recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        None
    }

  def findWithMaxId(jobType: String, jobName: String): Future[Option[JobDao]] =
    database.run(
      table
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
    database.run(table.filter(_.id === id).delete) recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        0
    }

  def all(): Future[Seq[JobDao]] =
    database.run(table.to[Seq].sortBy(_.id.desc).result) recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        List.empty[JobDao]
    }

  def save(job: JobDao): Future[Any] =
    database.run(table += job).recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
    }

  def save(jobs: Seq[JobDao]): Future[Option[Int]] =
    database.run(table ++= jobs) recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        None
    }
}

class JobDaoTable(tag: Tag) extends Table[JobDao](tag, "job") {
  def id = column[String]("id", O.PrimaryKey)

  def jobType = column[String]("job_type")

  def jobName = column[String]("job_name")

  def url = column[String]("url")

  def useProxy = column[Boolean]("use_proxy")

  def statusCode = column[Int]("status_code")

  def statusMessage = column[String]("status_message")

  def createdAt = column[DateTime]("created_at", SqlType("timestamp not null default CURRENT_TIMESTAMP"))

  def completedAt = column[DateTime]("completed_at")

  import MysqlRepo._
  def * =
    (id, jobType, jobName, url, useProxy, statusCode.?, statusMessage.?, createdAt, completedAt.?) <> ((JobDao.apply _).tupled, JobDao.unapply)
}
