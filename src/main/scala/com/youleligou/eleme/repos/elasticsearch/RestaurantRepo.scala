package com.youleligou.eleme.repos.elasticsearch

import com.google.inject.Inject
import com.youleligou.core.reps.ElasticSearchRepo
import com.youleligou.eleme.daos.RestaurantDao
import org.apache.spark.SparkContext
import org.elasticsearch.spark.rdd.EsSpark

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._
import scala.util.control.NonFatal

/**
  * Created by liangliao on 25/4/17.
  */
class RestaurantRepo @Inject()(val sparkContext: SparkContext) extends ElasticSearchRepo[RestaurantDao] {
  override val index: String = "eleme"
  override val typ: String   = "restaurant"

  override def save(records: Seq[RestaurantDao]): Future[Any] =
    Future {
      val rdd = sparkContext.makeRDD(records)
      EsSpark.saveToEs(rdd, s"$index/$typ", Map("es.mapping.id" -> "id"))
    } recover {
      case NonFatal(x) =>
        logger.warn("{} {}", this.getClass, x.getMessage)
    }

}
