package com.youleligou.crawler.daos

import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.{Date, UUID}

import com.youleligou.crawler.models.Job

/**
  * Created by liangliao on 18/4/17.
  */
case class JobDao(
    id: UUID,
    jobType: String,
    jobName: String,
    url: String,
    useProxy: Boolean,
    statusCode: Option[Int],
    statusMessage: Option[String],
    completedAt: Option[Date] = None,
    createdAt: Date = Timestamp.valueOf(LocalDateTime.now())
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
    completedAt = model.completedAt
  )

  implicit def convertSeq(source: Seq[Job])(implicit converter: Job => JobDao): Seq[JobDao] = source map converter
}
