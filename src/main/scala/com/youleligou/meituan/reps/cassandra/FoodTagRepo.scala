package com.youleligou.meituan.reps.cassandra

import com.datastax.spark.connector._
import com.google.inject.Inject
import com.youleligou.core.reps.CassandraRepo
import com.youleligou.meituan.daos.FoodTagDao
import org.apache.spark.SparkContext

/**
  * Created by liangliao on 25/4/17.
  */
class FoodTagRepo @Inject()(val sparkContext: SparkContext) extends CassandraRepo[FoodTagDao] {
  val keyspace: String = "meituan"
  val table: String    = "food_tags"

  def findById(id: Long): Option[FoodTagDao] = sparkContext.cassandraTable[FoodTagDao](keyspace, table).where("id = ?", id).collect().headOption
}
