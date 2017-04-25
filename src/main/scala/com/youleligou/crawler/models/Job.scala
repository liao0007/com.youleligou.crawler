package com.youleligou.crawler.models

import java.util.UUID

import com.youleligou.crawler.models.Job.FetchJobType
import org.joda.time.DateTime

/**
  * Created by liangliao on 25/4/17.
  */
case class Job(
    id: UUID = UUID.randomUUID(),
    jobType: String = FetchJobType,
    jobName: String,
    url: String,
    useProxy: Boolean = false,
    createdAt: DateTime = DateTime.now(),
    completedAt: Option[DateTime] = None,
    statusCode: Option[Int] = None,
    statusMessage: Option[String] = None
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
