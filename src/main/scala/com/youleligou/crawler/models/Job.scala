package com.youleligou.crawler.models

import java.sql.Timestamp
import java.time.Instant
import java.util.UUID

import com.youleligou.crawler.models.Job.FetchJobType

/**
  * Created by liangliao on 25/4/17.
  */
case class Job(
    id: UUID = UUID.randomUUID(),
    jobType: String = FetchJobType,
    jobName: String,
    url: String,
    useProxy: Boolean = false,
    statusCode: Option[Int] = None,
    statusMessage: Option[String] = None,
    completedAt: Option[Timestamp] = None
)

object Job {

  abstract class JobType(val name: String) {
    override def toString: String = name
  }

  case object FetchJobType extends JobType("Fetch")

  object JobType {
    implicit def jobToString(jobType: JobType): String = jobType.toString
  }

}
