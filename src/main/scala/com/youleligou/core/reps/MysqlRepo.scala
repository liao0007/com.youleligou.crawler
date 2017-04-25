package com.youleligou.core.reps

import java.sql.Timestamp

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

object MysqlRepo {
  implicit def dateTime = MappedColumnType.base[DateTime, Timestamp](
    dt => new Timestamp(dt.getMillis),
    ts => new DateTime(ts.getTime)
  )
  implicit def localDate = MappedColumnType.base[LocalDate, Timestamp](
    date => new Timestamp(date.toDateTime(new LocalTime(0, 0)).getMillis),
    ts => new LocalDate(ts.getTime)
  )
}
