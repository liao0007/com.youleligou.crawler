package com.youleligou.eleme.repos.cassandra

import com.google.inject.Inject
import com.datastax.spark.connector._
import com.youleligou.core.reps.CassandraRepo
import com.youleligou.eleme.daos.RestaurantDao
import com.youleligou.eleme.daos.accumulate.CategoryAccumulate
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

/**
  * Created by liangliao on 25/4/17.
  */
class CategoryRepo @Inject()(val sparkContext: SparkContext) extends CassandraRepo[CategoryAccumulate] {
  val keyspace: String = "eleme"
  val table: String    = "categories"

  def findById(id: Long): Option[CategoryAccumulate] = sparkContext.cassandraTable[CategoryAccumulate](keyspace, table).where("id = ?", id).collect().headOption
}
