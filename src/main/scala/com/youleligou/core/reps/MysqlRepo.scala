package com.youleligou.core.reps

import com.youleligou.core.daos.Dao
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Future

/**
  * Created by liangliao on 25/4/17.
  */
abstract class MysqlRepo[T <: Dao] extends Repo[T] {
  val database: Database

  def save(record: T): Future[Any]

  def save(records: Seq[T]): Future[Any]

  def all(): Future[Seq[T]]

}