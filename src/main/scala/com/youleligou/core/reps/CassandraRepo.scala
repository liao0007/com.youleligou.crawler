package com.youleligou.core.reps

import com.datastax.spark.connector._
import com.datastax.spark.connector.rdd.CassandraRDD
import com.youleligou.core.daos.Dao
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.reflect.ClassTag
import scala.reflect.runtime.universe._
import scala.util.control.NonFatal

/**
  * Created by liangliao on 25/4/17.
  */
abstract class CassandraRepo[T <: Dao](implicit classTag: ClassTag[T], typeTag: TypeTag[T]) extends Repo[T] {
  def keyspace: String
  def sparkContext: SparkContext

  override val schema: String = keyspace

  def save(record: T): Future[Any] = Future {
    save(Seq(record))
  }

  def save(records: Seq[T]): Future[Any] = {
    val collection: RDD[T] = sparkContext.parallelize(records)
    save(collection)
  }

  def save(records: RDD[T]): Future[Unit] =
    Future {
      records.saveToCassandra(keyspace, table)
    } recover {
      case NonFatal(x) =>
        logger.warn("{} {}", this.getClass, x.getMessage)
    }

  def rddAll(): Future[CassandraRDD[T]] =
    Future {
      sparkContext.cassandraTable[T](keyspace, table)
    } recover {
      case NonFatal(x) =>
        logger.warn("{} {}", this.getClass, x.getMessage)
        sparkContext.emptyCassandraTable[T](keyspace, table)
    }

  def all(): Future[Seq[T]] = rddAll() map {
    _.collect().toSeq
  }

}
