package com.youleligou.processors

import com.datastax.spark.connector._
import com.datastax.spark.connector.rdd.CassandraRDD
import com.google.inject.Inject
import com.youleligou.eleme.daos.{FoodSnapshotDao, FoodSnapshotSearch}
import com.youleligou.eleme.models.{Category, Food, Restaurant}
import org.apache.spark.SparkContext

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by liangliao on 4/5/17.
  */
class MenuProcessor @Inject()(sparkContext: SparkContext,
                              foodSnapshotRepo: com.youleligou.eleme.repos.cassandra.FoodSnapshotRepo,
                              restaurantRepo: com.youleligou.eleme.repos.cassandra.RestaurantRepo,
                              categoryRepo: com.youleligou.eleme.repos.cassandra.CategoryRepo,
                              foodSnapshotSearchRepo: com.youleligou.eleme.repos.elasticsearch.FoodSnapshotRepo) {

  def reindex(): Future[Unit] =
    foodSnapshotRepo.allRdd().map { (foodSnapshotDaoRdd: CassandraRDD[FoodSnapshotDao]) =>
      val foodSnapshotSearchRdd = foodSnapshotDaoRdd.flatMap { (foodSnapshotDao: FoodSnapshotDao) =>
        val food: Food = foodSnapshotDao
        restaurantRepo.findById(food.restaurantId) flatMap { restaurantDao =>
          implicit val restaurant: Restaurant = restaurantDao
          categoryRepo.findById(food.categoryId) map { categoryDao =>
            implicit val category: Category            = categoryDao
            val foodSnapshotSearch: FoodSnapshotSearch = food
            foodSnapshotSearch
          }
        }
      }
      foodSnapshotSearchRepo.save(foodSnapshotSearchRdd)
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
