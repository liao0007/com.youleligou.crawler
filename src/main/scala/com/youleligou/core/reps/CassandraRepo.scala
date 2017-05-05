package com.youleligou.core.reps

import com.datastax.spark.connector._
import com.datastax.spark.connector.rdd.{CassandraRDD, CassandraTableScanRDD}
import org.apache.spark.SparkContext

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.reflect.ClassTag
import scala.reflect.runtime.universe._
import scala.util.control.NonFatal

/**
  * Created by liangliao on 25/4/17.
  */
abstract class CassandraRepo[T <: Serializable](implicit classTag: ClassTag[T], typeTag: TypeTag[T]) extends Repo[T] {
  def keyspace: String
  def sparkContext: SparkContext

  override val schema: String = keyspace

  def save(record: T): Future[Any] = Future {
    save(Seq(record))
  }

  def save(records: Seq[T]): Future[Any] =
    Future {
      val collection = sparkContext.parallelize(records)
      collection.saveToCassandra(keyspace, table)
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

  def all(): Future[Seq[T]] =
    Future {
      sparkContext.cassandraTable[T](keyspace, table).collect().toSeq
    } recover {
      case NonFatal(x) =>
        logger.warn("{} {}", this.getClass, x.getMessage)
        Seq.empty[T]
    }

}
