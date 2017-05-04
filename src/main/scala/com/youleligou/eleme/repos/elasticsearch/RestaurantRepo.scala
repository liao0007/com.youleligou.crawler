package com.youleligou.eleme.repos.elasticsearch

import com.google.inject.Inject
import com.youleligou.core.reps.ElasticSearchRepo
import com.youleligou.eleme.daos.RestaurantSearch
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.elasticsearch.spark.rdd.EsSpark

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future
import scala.util.control.NonFatal

/**
  * Created by liangliao on 25/4/17.
  */
class RestaurantRepo @Inject()(val sparkContext: SparkContext) extends ElasticSearchRepo[RestaurantSearch] {
  override val index: String = "eleme-restaurant"
  override val typ: String   = "latest"

  override def save(rdd: RDD[RestaurantSearch]): Future[Any] =
    Future {
      EsSpark.saveToEs(rdd, s"$index/$typ", Map("es.mapping.id" -> "id"))
    } recover {
      case NonFatal(x) =>
        logger.warn("{} {}", this.getClass, x.getMessage)
    }

}
