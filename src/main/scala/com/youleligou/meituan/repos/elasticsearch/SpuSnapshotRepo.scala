package com.youleligou.meituan.repos.elasticsearch

import com.google.inject.Inject
import com.youleligou.core.reps.ElasticSearchRepo
import com.youleligou.meituan.daos.SpuSnapshotDaoSearch
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.elasticsearch.spark.rdd.EsSpark

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future
import scala.util.control.NonFatal

/**
  * Created by liangliao on 25/4/17.
  */
class SpuSnapshotRepo @Inject()(val sparkContext: SparkContext) extends ElasticSearchRepo[SpuSnapshotDaoSearch] {
  override val index: String = "meituan-spu"
  override val typ: String   = "snapshot"

  override def save(rdd: RDD[SpuSnapshotDaoSearch]): Future[Any] =
    Future {
      EsSpark.saveToEs(rdd, s"$index/$typ", Map("es.mapping.id" -> "id"))
    } recover {
      case NonFatal(x) =>
        logger.warn("{} {}", this.getClass, x.getMessage)
    }

}
