package com.youleligou.processors

import com.datastax.spark.connector._
import com.datastax.spark.connector.rdd.CassandraRDD
import com.google.inject.Inject
import com.youleligou.eleme.daos.{CategoryDao, FoodSnapshotDao, FoodSnapshotSearch, RestaurantDao}
import com.youleligou.eleme.models.{Category, Food, Restaurant}
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

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

  def reindex(): Future[Unit] = {
    restaurantRepo.rddAll() map { (restaurantDaoRdd: CassandraRDD[RestaurantDao]) =>
      restaurantDaoRdd map { (restaurantDao: RestaurantDao) =>
        implicit val restaurant: Restaurant = restaurantDao
        foodSnapshotRepo.rddFindByRestaurantId(restaurantDao.id) groupBy (_.categoryId) map {
          case (categoryId, foodSnapshotDaos) =>
            val foodSnapshotSearchRdd: RDD[FoodSnapshotSearch] = categoryRepo.rddFindById(categoryId) flatMap { (categoryDao: CategoryDao) =>
              implicit val category: Category = categoryDao
              foodSnapshotDaos map { (foodSnapshotDao: FoodSnapshotDao) =>
                val food: Food                             = foodSnapshotDao
                val foodSnapshotSearch: FoodSnapshotSearch = food
                foodSnapshotSearch
              }
            }
            foodSnapshotSearchRepo.save(foodSnapshotSearchRdd)
        }
      }
    }
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
