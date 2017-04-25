package com.youleligou.core.reps

import org.apache.spark.SparkContext

import scala.concurrent.Future

/**
  * Created by liangliao on 25/4/17.
  */
trait Repo[T] {
  val schema: String
  val table: String
  val sparkContext: SparkContext

  def save(record: T): Future[Unit]

  def save(records: Seq[T]): Future[Unit]

  def all(): Future[Seq[T]]
}
