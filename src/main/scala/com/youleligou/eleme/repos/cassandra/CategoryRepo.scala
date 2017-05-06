package com.youleligou.eleme.repos.cassandra

import com.datastax.spark.connector._
import com.google.inject.Inject
import com.youleligou.core.reps.CassandraRepo
import com.youleligou.eleme.daos.CategoryDao
import org.apache.spark.SparkContext

/**
  * Created by liangliao on 25/4/17.
  */
class CategoryRepo @Inject()(val sparkContext: SparkContext) extends CassandraRepo[CategoryDao] {
  val keyspace: String = "eleme"
  val table: String    = "categories"

  def findById(id: Long): Option[CategoryDao] = sparkContext.cassandraTable[CategoryDao](keyspace, table).where("id = ?", id).collect().headOption
}
