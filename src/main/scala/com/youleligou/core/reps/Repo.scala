package com.youleligou.core.reps

import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.Future

/**
  * Created by liangliao on 25/4/17.
  */
trait Repo[T] extends LazyLogging {
  def schema: String
  def table: String

  def save(record: T): Future[Any]

  def save(records: Seq[T]): Future[Any]

  def all(): Future[Seq[T]]
}
