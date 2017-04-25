package com.youleligou.crawler.daos

import java.util.UUID

import com.youleligou.crawler.daos.JobDao.JobType
import com.youleligou.crawler.models.Job
import org.joda.time.DateTime

/**
  * Created by liangliao on 18/4/17.
  */
case class JobDao(
    id: UUID = UUID.randomUUID(),
    jobType: String = JobType.Fetch.toString,
    jobName: String,
    url: String,
    useProxy: Boolean = false,
    statusCode: Option[Int] = None,
    statusMessage: Option[String] = None,
    createdAt: DateTime = DateTime.now(),
    completedAt: Option[DateTime] = None
)

object JobDao {
  object JobType extends Enumeration {
    val Fetch = Value
  }

  implicit def fromModel(model: Job): JobDao = JobDao(
    id = model.id,
    jobType = model.jobType,
    jobName = model.jobName,
    url = model.url,
    useProxy = model.useProxy,
    statusCode = model.statusCode,
    statusMessage = model.statusMessage,
    createdAt = model.createdAt,
    completedAt = model.completedAt
  )

  implicit def convertSeq(source: Seq[Job])(implicit converter: Job => JobDao): Seq[JobDao] = source map converter
}
