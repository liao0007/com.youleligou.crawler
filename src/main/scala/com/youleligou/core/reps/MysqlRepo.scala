package com.youleligou.core.reps

import java.sql.{Date, Timestamp}
import java.util.UUID

import org.joda.time.{DateTime, LocalDate, LocalTime}
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Future

/**
  * Created by liangliao on 25/4/17.
  */
trait MysqlRepo[T] extends Repo[T] {
  val database: Database

  def save(record: T): Future[Unit]

  def save(records: Seq[T]): Future[Unit]

  def all(): Future[Seq[T]]

}