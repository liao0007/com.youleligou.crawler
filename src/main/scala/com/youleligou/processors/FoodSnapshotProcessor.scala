package com.youleligou.processors

import com.datastax.spark.connector._
import com.google.inject.Inject
import com.youleligou.core.reps.CassandraRepo
import com.youleligou.eleme.daos.FoodSnapshotDao
import org.apache.spark.SparkContext

/**
  * Created by liangliao on 4/5/17.
  */
class FoodSnapshotProcessor @Inject()(sparkContext: SparkContext, foodSnapshotRepo: CassandraRepo[FoodSnapshotDao]) {

  def run(): Unit = {

    sparkContext
      .cassandraTable[FoodSnapshotDao]("eleme", "food_snapshots")
      .filter(_.monthSales > 0)
      .groupBy(_.name)
      .map { grouped =>
        grouped._1 -> grouped._2.map(_.monthSales).sum
      }
      .sortBy(_._2)
      .collect() foreach { sorted =>
      println(sorted._1 + "," + sorted._2)
    }

  }

}
