package com.youleligou.eleme.repos.mysql

import com.google.inject.Inject
import com.youleligou.core.reps.MysqlRepo
import com.youleligou.eleme.daos.FoodDao
import org.joda.time.DateTime
import slick.jdbc.MySQLProfile.api._
import slick.lifted.Tag

import scala.concurrent.Future
import scala.util.control.NonFatal

/**
  * Created by liangliao on 25/4/17.
  */
class FoodDaoRepo @Inject()(val schema: String = "cancan", val table: String = "foods", val database: Database) extends MysqlRepo[FoodDao] {
  val FoodDaos: TableQuery[FoodDaoTable] = TableQuery[FoodDaoTable]

  def find(itemId: Long): Future[Option[FoodDao]] =
    database.run(FoodDaos.filter(_.itemId === itemId).result.headOption) recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        None
    }

  def delete(itemId: Long): Future[Int] =
    database.run(FoodDaos.filter(_.itemId === itemId).delete) recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        0
    }

  def all(): Future[Seq[FoodDao]] =
    database.run(FoodDaos.to[Seq].result) recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        List.empty[FoodDao]
    }

  def save(restaurant: FoodDao): Future[Any] =
    database.run(FoodDaos += restaurant) recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
    }

  def save(restaurants: Seq[FoodDao]): Future[Option[Int]] =
    database.run(FoodDaos ++= restaurants) recover {
      case NonFatal(x) if !x.getMessage.contains("Duplicate entry") =>
        logger.warn(x.getMessage)
        None
    }

  def insertOrUpdate(restaurant: FoodDao): Future[Int] =
    database.run {
      FoodDaos.insertOrUpdate(restaurant)
    } recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        0
    }

}

class FoodDaoTable(tag: Tag) extends Table[FoodDao](tag, "foods") {
  def itemId       = column[Long]("item_id", O.PrimaryKey)
  def restaurantId = column[Long]("restaurant_id")
  def categoryId   = column[Long]("category_id")
  def name         = column[String]("name")
  def description  = column[String]("description")
  def monthSales   = column[Int]("month_sales")
  def rating       = column[Float]("rating")
  def ratingCount  = column[Int]("rating_count")
  def satisfyCount = column[Int]("satisfy_count")
  def satisfyRate  = column[Float]("satisfy_rate")
  def createdAt    = column[DateTime]("created_at")

  import MysqlRepo._
  def * =
    (itemId, restaurantId, categoryId, name, description, monthSales, rating, ratingCount, satisfyCount, satisfyRate, createdAt) <> ((FoodDao.apply _).tupled, FoodDao.unapply)

}
