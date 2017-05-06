package com.youleligou.core.reps

import com.youleligou.core.daos.Dao
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.elasticsearch.spark.rdd.EsSpark

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future
import scala.reflect.ClassTag
import scala.reflect.runtime.universe._
import scala.util.control.NonFatal

/**
  * Created by liangliao on 2/5/17.
  */
abstract class ElasticSearchRepo[T <: Dao](implicit classTag: ClassTag[T], typeTag: TypeTag[T]) extends Repo[T] {
  def index: String
  def typ: String
  def sparkContext: SparkContext

  override val schema: String = index
  override val table: String  = typ

  def save(rdd: RDD[T]): Future[Any] =
    Future {
      EsSpark.saveToEs(rdd, s"$index/$typ")
    } recover {
      case NonFatal(x) =>
        logger.warn("{} {}", this.getClass, x.getMessage)
    }

  def save(records: Seq[T]): Future[Any] = {
    val rdd = sparkContext.makeRDD(records)
    save(rdd)
  }

  def save(record: T): Future[Any] = save(Seq(record))

  def all(): Future[Seq[T]] = ???

}
