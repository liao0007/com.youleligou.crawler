package com.youleligou.crawler.dao

import java.sql.Timestamp

import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.dao.CrawlerJob.{FetchJobType, JobType}
import com.youleligou.crawler.dao.schema.CanCan
import slick.jdbc.MySQLProfile.api._
import slick.lifted.Tag
import slick.sql.SqlProfile.ColumnOption.SqlType

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._

case class CrawlerJob(
                       id: Long = 0,
                       jobType: String = FetchJobType,
                       jobName: String,
                       url: String,
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

class CrawlerJobRepo extends LazyLogging {
  val CrawlerJobs: TableQuery[CrawlerJobTable] = TableQuery[CrawlerJobTable]

  def find(id: Long): Future[Option[CrawlerJob]] =
    CanCan.db.run(CrawlerJobs.filter(_.id === id).result.headOption)

  def findWithMaxId(jobType: JobType, jobName: String): Future[Option[CrawlerJob]] =
    CanCan.db.run(
      CrawlerJobs
        .filter(job => job.jobType === jobType.toString && job.jobName === jobName)
        .sortBy(_.id.desc)
        .take(1)
        .result
        .headOption)

  def delete(id: Long): Future[Int] =
    CanCan.db.run(CrawlerJobs.filter(_.id === id).delete)

  def all(): Future[List[CrawlerJob]] =
    CanCan.db.run(CrawlerJobs.to[List].result)

  def create(job: CrawlerJob): Future[Long] =
    CanCan.db.run(CrawlerJobs returning CrawlerJobs.map(_.id) += job).recover {
      case t: Throwable =>
        logger.error(t.getMessage)
        0l
    }

  def create(jobs: List[CrawlerJob]): Future[Option[Int]] =
    CanCan.db.run(CrawlerJobs ++= jobs)
}

class CrawlerJobTable(tag: Tag) extends Table[CrawlerJob](tag, "crawler_job") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def jobType = column[String]("job_type")

  def jobName = column[String]("job_name")

  def url = column[String]("url")

  def createdAt = column[Timestamp]("created_at", SqlType("timestamp not null default CURRENT_TIMESTAMP"))

  def statusCode = column[Int]("status_code")

  def statusMessage = column[String]("status_message")

  def * =
    (id, jobType, jobName, url, createdAt, statusCode.?, statusMessage.?) <> ((CrawlerJob.apply _).tupled, CrawlerJob.unapply)

}
