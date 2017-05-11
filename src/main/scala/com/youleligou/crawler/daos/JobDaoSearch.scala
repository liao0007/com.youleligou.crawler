package com.youleligou.crawler.daos

import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.{Date, UUID}

import com.youleligou.core.daos.Dao
import com.youleligou.crawler.models.Job

/**
  * Created by liangliao on 18/4/17.
  */
case class JobDaoSearch(
    id: UUID,
    jobType: String,
    jobName: String,
    url: String,
    useProxy: Boolean,
    statusCode: Option[Int],
    statusMessage: Option[String],
    completedAt: Option[Date] = None,
    //spark cassandra connector not support java.sql.Timestamp, use java.util.Date instead
    createdAt: Date = Timestamp.valueOf(LocalDateTime.now())
) extends Dao

object JobDaoSearch {
  implicit def fromDao(dao: JobDao): JobDaoSearch =
    JobDaoSearch(
      id = dao.id,
      jobType = dao.jobType,
      jobName = dao.jobName,
      url = dao.url,
      useProxy = dao.useProxy,
      statusCode = dao.statusCode,
      statusMessage = dao.statusMessage,
      completedAt = dao.completedAt,
      //spark cassandra connector not support java.sql.Timestamp, use java.util.Date instead
      createdAt = dao.createdAt
    )
  implicit def fromDao(source: Seq[JobDao])(implicit converter: JobDao => JobDaoSearch): Seq[JobDaoSearch] =
    source map converter



  implicit def fromModel(model: Job): JobDaoSearch = JobDaoSearch(
    id = model.id,
    jobType = model.jobType,
    jobName = model.jobName,
    url = model.url,
    useProxy = model.useProxy,
    statusCode = model.statusCode,
    statusMessage = model.statusMessage,
    completedAt = model.completedAt
  )

  implicit def fromModel(source: Seq[Job])(implicit converter: Job => JobDaoSearch): Seq[JobDaoSearch] = source map converter
}
