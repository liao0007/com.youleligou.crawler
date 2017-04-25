package com.youleligou.crawler.daos

import com.youleligou.crawler.models.Job
import org.joda.time.DateTime

/**
  * Created by liangliao on 18/4/17.
  */
case class JobDao(
    id: String,
    jobType: String,
    jobName: String,
    url: String,
    useProxy: Boolean,
    statusCode: Option[Int],
    statusMessage: Option[String],
    createdAt: DateTime,
    completedAt: Option[DateTime]
)

object JobDao {
  object JobType extends Enumeration {
    val Fetch = Value
  }

  implicit def fromModel(model: Job): JobDao = JobDao(
    id = model.id.toString,
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
