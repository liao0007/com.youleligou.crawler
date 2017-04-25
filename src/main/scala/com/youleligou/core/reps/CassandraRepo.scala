package com.youleligou.core.reps

import com.datastax.spark.connector._
import org.apache.spark.SparkContext

import scala.concurrent.Future

/**
  * Created by liangliao on 25/4/17.
  */
trait CassandraRepo[T] extends Repo[T] {
  val keyspace: String
  val sparkContext: SparkContext

  override val schema: String = keyspace

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
