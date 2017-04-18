package com.youleligou.crawler.daos.mysql

import java.sql.Timestamp

import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.daos.mysql.CrawlerJob.{FetchJobType, JobType}
import slick.jdbc.MySQLProfile.api._
import slick.lifted.Tag
import slick.sql.SqlProfile.ColumnOption.SqlType

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future
import scala.util.control.NonFatal

case class CrawlerJob(
    id: Long = 0,
    jobType: String = FetchJobType,
    jobName: String,
    url: String,
    useProxy: Boolean = false,
    createdAt: Timestamp = new Timestamp(System.currentTimeMillis()),
    statusCode: Option[Int] = None,
    statusMessage: Option[String] = None
)

object CrawlerJob {

  abstract class JobType(val name: String) {
    override def toString: String = name
  }

  case object FetchJobType extends JobType("Fetch")

  object JobType {
    implicit def jobToString(jobType: JobType): String = jobType.toString
  }

}

class CrawlerJobRepo @Inject()(@Named(schemas.CanCan) database: Database) extends LazyLogging {
  val CrawlerJobs: TableQuery[CrawlerJobTable] = TableQuery[CrawlerJobTable]

  def find(id: Long): Future[Option[CrawlerJob]] =
    database.run(CrawlerJobs.filter(_.id === id).result.headOption) recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        None
    }

  def findWithMaxId(jobType: JobType, jobName: String): Future[Option[CrawlerJob]] =
    database.run(
      CrawlerJobs
        .filter(job => job.jobType === jobType.toString && job.jobName === jobName)
        .sortBy(_.id.desc)
        .take(1)
        .result
        .headOption) recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        None
    }

  def delete(id: Long): Future[Int] =
    database.run(CrawlerJobs.filter(_.id === id).delete) recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        0
    }

  def all(): Future[List[CrawlerJob]] =
    database.run(CrawlerJobs.to[List].sortBy(_.id.desc).result) recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        List.empty[CrawlerJob]
    }

  def create(job: CrawlerJob): Future[Long] =
    database.run(CrawlerJobs returning CrawlerJobs.map(_.id) += job).recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        0L
    } recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        0L
    }

  def create(jobs: List[CrawlerJob]): Future[Option[Int]] =
    database.run(CrawlerJobs ++= jobs) recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        None
    }
}

class CrawlerJobTable(tag: Tag) extends Table[CrawlerJob](tag, "crawler_job") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def jobType = column[String]("job_type")

  def jobName = column[String]("job_name")

  def url = column[String]("url")

  def useProxy = column[Boolean]("use_proxy")

  def createdAt = column[Timestamp]("created_at", SqlType("timestamp not null default CURRENT_TIMESTAMP"))

  def statusCode = column[Int]("status_code")

  def statusMessage = column[String]("status_message")

  def * =
    (id, jobType, jobName, url, useProxy, createdAt, statusCode.?, statusMessage.?) <> ((CrawlerJob.apply _).tupled, CrawlerJob.unapply)

}
