package com.youleligou.core.reps

import java.sql.Timestamp

import com.typesafe.scalalogging.LazyLogging
import org.joda.time.DateTime
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Future

/**
  * Created by liangliao on 25/4/17.
  */
trait MysqlRepo[T, Table] extends Repo[T] with LazyLogging {
  val table: TableQuery[Table] = TableQuery[Table]
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
}
