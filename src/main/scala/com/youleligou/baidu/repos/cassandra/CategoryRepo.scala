package com.youleligou.baidu.repos.cassandra

import com.datastax.spark.connector._
import com.google.inject.Inject
import com.youleligou.baidu.daos.CategoryDao
import com.youleligou.core.reps.CassandraRepo
import org.apache.spark.SparkContext

/**
  * Created by liangliao on 25/4/17.
  */
class CategoryRepo @Inject()(val sparkContext: SparkContext) extends CassandraRepo[CategoryDao] {
  val keyspace: String = "baidu"
  val table: String    = "categories"

  def findById(categoryId: Long): Option[CategoryDao] = sparkContext.cassandraTable[CategoryDao](keyspace, table).where("category_id = ?", categoryId).collect().headOption
}
