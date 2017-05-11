package com.youleligou.crawler.repos.elasticsearch

import com.google.inject.Inject
import com.youleligou.core.reps.ElasticSearchRepo
import com.youleligou.crawler.daos.JobDaoSearch
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.elasticsearch.spark.rdd.EsSpark

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future
import scala.util.control.NonFatal

/**
  * Created by liangliao on 25/4/17.
  */
class JobRepo @Inject()(val sparkContext: SparkContext) extends ElasticSearchRepo[JobDaoSearch] {
  override val index: String = "crawler"
  override val typ: String   = "job"

  override def save(rdd: RDD[JobDaoSearch]): Future[Any] =
    Future {
      EsSpark.saveToEs(rdd, s"$index/$typ", Map("es.mapping.id" -> "id"))
    } recover {
      case NonFatal(x) =>
        logger.warn("{} {}", this.getClass, x.getMessage)
    }

}
