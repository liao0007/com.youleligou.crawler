package com.youleligou.core.reps

import com.datastax.spark.connector._
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Future

/**
  * Created by liangliao on 25/4/17.
  */
trait MysqlRepo[T] extends Repo[T] {
  val database: Database

  def save(record: T): Future[Unit] = Future {
    save(Seq(record))
  }

  def save(records: Seq[T]): Future[Unit] = Future {
    val collection = sparkContext.parallelize(records)
    collection.saveToCassandra(keyspace, table)
  }

  def all(): Future[Seq[T]] = Future {
    sparkContext.cassandraTable[T](keyspace, table).collect().toSeq
  }

}
