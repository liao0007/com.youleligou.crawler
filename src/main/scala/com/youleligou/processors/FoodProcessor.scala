package com.youleligou.processors

import com.datastax.spark.connector._
import com.datastax.spark.connector.rdd.CassandraRDD
import com.google.inject.Inject
import com.youleligou.eleme.daos.{FoodSnapshotDao, FoodSnapshotSearch}
import com.youleligou.eleme.models.Food
import org.apache.spark.SparkContext

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by liangliao on 4/5/17.
  */
class FoodProcessor @Inject()(sparkContext: SparkContext,
                              foodSnapshotRepo: com.youleligou.eleme.repos.cassandra.FoodSnapshotRepo,
                              foodSnapshotSearchRepo: com.youleligou.eleme.repos.elasticsearch.FoodSnapshotRepo) {
//  implicit restaurantModel: Restaurant, categoryModel: Category
  def reindex() = {
//    foodSnapshotRepo.allRdd().flatMap { (foodSnapshotDaoRdd: CassandraRDD[FoodSnapshotDao]) =>
//      val foodSnapshotSearchRdd = foodSnapshotDaoRdd.map { (foodSnapshotDao: FoodSnapshotDao) =>
//        val food: Food                             = foodSnapshotDao
//        val foodSnapshotSearch: FoodSnapshotSearch = food
//        foodSnapshotSearch
//      }
//      foodSnapshotSearchRepo.save(foodSnapshotSearchRdd)
//    }
  }

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
