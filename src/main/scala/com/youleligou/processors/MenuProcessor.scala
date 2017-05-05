package com.youleligou.processors

import com.datastax.spark.connector._
import com.google.inject.Inject
import com.youleligou.eleme.daos.{CategoryDao, FoodSnapshotDao, FoodSnapshotSearch, RestaurantDao}
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

  def reindex(): Future[Seq[Option[Future[Any]]]] = {
    restaurantRepo.all() map { (restaurantDao: Seq[RestaurantDao]) =>
      restaurantDao flatMap { (restaurantDao: RestaurantDao) =>
        implicit val restaurant: Restaurant = restaurantDao
        foodSnapshotRepo.findByRestaurantId(restaurantDao.id) groupBy (_.categoryId) map {
          case (categoryId, foodSnapshotDaos) =>
            categoryRepo.findById(categoryId) map { (categoryDao: CategoryDao) =>
              implicit val category: Category = categoryDao
              foodSnapshotDaos map { (foodSnapshotDao: FoodSnapshotDao) =>
                val food: Food                             = foodSnapshotDao
                val foodSnapshotSearch: FoodSnapshotSearch = food
                foodSnapshotSearch
              }
            } map {
              foodSnapshotSearchRepo.save(_)
            }
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
