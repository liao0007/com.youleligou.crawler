package com.youleligou.core.reps

import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Future

/**
  * Created by liangliao on 25/4/17.
  */
abstract class MysqlRepo[T] extends Repo[T] {
  val database: Database

  def save(record: T): Future[Any]

  def save(records: Seq[T]): Future[Any]

  def all(): Future[Seq[T]]

}