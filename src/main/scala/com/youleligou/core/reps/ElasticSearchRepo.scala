package com.youleligou.core.reps

import org.apache.spark.SparkContext
import org.elasticsearch.spark.rdd.EsSpark

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._
import scala.reflect.ClassTag
import scala.reflect.runtime.universe._

/**
  * Created by liangliao on 2/5/17.
  */
abstract class ElasticSearchRepo[T](implicit classTag: ClassTag[T], typeTag: TypeTag[T]) extends Repo[T] {
  def index: String
  def typ: String
  def sparkContext: SparkContext

  override val schema: String = index
  override val table: String  = typ

  def save(record: T): Future[Any] = Future {
    save(Seq(record))
  }

  def save(records: Seq[T]): Future[Any] = Future {
    val rdd = sparkContext.makeRDD(records)
    EsSpark.saveToEs(rdd, s"$index/$typ")
  }

  def all(): Future[Seq[T]] = ???

}
