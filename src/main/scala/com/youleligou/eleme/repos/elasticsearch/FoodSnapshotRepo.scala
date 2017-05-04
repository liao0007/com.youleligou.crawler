package com.youleligou.eleme.repos.elasticsearch

import com.google.inject.Inject
import com.youleligou.core.reps.ElasticSearchRepo
import com.youleligou.eleme.daos.FoodSnapshotSearch
import org.apache.spark.SparkContext
import org.elasticsearch.spark.rdd.EsSpark

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future
import scala.util.control.NonFatal

/**
  * Created by liangliao on 25/4/17.
  */
class FoodSnapshotRepo @Inject()(val sparkContext: SparkContext) extends ElasticSearchRepo[FoodSnapshotSearch] {
  override val index: String = "eleme-food"
  override val typ: String   = "snapshot"

  override def save(records: Seq[FoodSnapshotSearch]): Future[Any] =
    Future {
      val rdd = sparkContext.makeRDD(records)
      EsSpark.saveToEs(rdd, s"$index/$typ", Map("es.mapping.id" -> "id"))
    } recover {
      case NonFatal(x) =>
        logger.warn("{} {}", this.getClass, x.getMessage)
    }

}
