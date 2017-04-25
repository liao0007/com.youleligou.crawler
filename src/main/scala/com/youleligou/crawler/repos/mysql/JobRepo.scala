package com.youleligou.crawler.repos.mysql

import java.util.UUID

import com.github.tototoshi.slick.MySQLJodaSupport._
import com.google.inject.Inject
import com.youleligou.core.reps.MysqlRepo
import com.youleligou.crawler.daos.JobDao
import org.joda.time.DateTime
import slick.jdbc.MySQLProfile.api._
import slick.lifted.Tag

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.control.NonFatal
/**
  * Created by liangliao on 25/4/17.
  */
class JobDaoRepo @Inject()(val schema: String = "cancan", val table: String = "job", val database: Database) extends MysqlRepo[JobDao] {
  val JobDaos: TableQuery[JobDaoTable] = TableQuery[JobDaoTable]

  def find(id: UUID): Future[Option[JobDao]] =
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

  def delete(id: UUID): Future[Int] =
    database.run(JobDaos.filter(_.id === id).delete) recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        0
    }

  def all(): Future[Seq[JobDao]] =
    database.run(JobDaos.to[Seq].sortBy(_.id.desc).result) recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        List.empty[JobDao]
    }

  def save(job: JobDao): Future[Any] =
    database.run(JobDaos += job).recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
    }

  def save(jobs: Seq[JobDao]): Future[Option[Int]] =
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

  def statusCode = column[Int]("status_code")

  def statusMessage = column[String]("status_message")

  def createdAt = column[DateTime]("created_at")

  def completedAt = column[DateTime]("completed_at")

  def * =
    (id, jobType, jobName, url, useProxy, statusCode.?, statusMessage.?, createdAt, completedAt.?) <> ((JobDao.apply _).tupled, JobDao.unapply)
}
