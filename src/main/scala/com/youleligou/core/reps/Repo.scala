package com.youleligou.core.reps

import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.Future

/**
  * Created by liangliao on 25/4/17.
  */
trait Repo[T] extends LazyLogging {
  val schema: String
  val table: String

  def save(record: T): Future[Any]

  def save(records: Seq[T]): Future[Any]

  def all(): Future[Seq[T]]
}
