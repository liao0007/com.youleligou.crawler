package com.youleligou.baidu.repos.elasticsearch

import com.google.inject.Inject
import com.youleligou.baidu.daos.DishSnapshotDaoSearch
import com.youleligou.core.reps.ElasticSearchRepo
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.elasticsearch.spark.rdd.EsSpark

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future
import scala.util.control.NonFatal

/**
  * Created by liangliao on 25/4/17.
  */
class DishSnapshotRepo @Inject()(val sparkContext: SparkContext) extends ElasticSearchRepo[DishSnapshotDaoSearch] {
  override val index: String = "baidu-dish"
  override val typ: String   = "snapshot"

  override def save(rdd: RDD[DishSnapshotDaoSearch]): Future[Any] =
    Future {
      EsSpark.saveToEs(rdd, s"$index/$typ", Map("es.mapping.id" -> "id"))
    } recover {
      case NonFatal(x) =>
        logger.warn("{} {}", this.getClass, x.getMessage)
    }

}
