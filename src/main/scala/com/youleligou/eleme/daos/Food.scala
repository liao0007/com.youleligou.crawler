package com.youleligou.eleme.daos

import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.daos.mysql.schemas
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import slick.jdbc.MySQLProfile.api._
import slick.lifted.Tag

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future
import scala.util.control.NonFatal

case class Food(
    id: Option[Long],
    itemId: Long = 0,
    restaurantId: Long,
    categoryId: Long,
    name: String,
    description: String,
    monthSales: Int,
    rating: Float,
    ratingCount: Int,
    satisfyCount: Int,
    satisfyRate: Float
)

object Food {
  implicit val restaurantReads: Reads[Food] = (
    (JsPath \ "id").readNullable[Long] and
      (JsPath \ "item_id").read[String].map(_.toLong) and
      (JsPath \ "restaurant_id").read[Long] and
      (JsPath \ "category_id").read[Long] and
      (JsPath \ "name").read[String] and
      (JsPath \ "description").read[String] and
      (JsPath \ "month_sales").read[Int] and
      (JsPath \ "rating").read[Float] and
      (JsPath \ "rating_count").read[Int] and
      (JsPath \ "satisfy_count").read[Int] and
      (JsPath \ "satisfy_rate").read[Float]
  )(Food.apply _)
}

class FoodRepo @Inject()(@Named(schemas.CanCan) database: Database) extends LazyLogging {
  val Foods: TableQuery[FoodTable] = TableQuery[FoodTable]

  def find(id: Long): Future[Option[Food]] =
    database.run(Foods.filter(_.id === id).result.headOption) recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        None
    }

  def delete(id: Long): Future[Int] =
    database.run(Foods.filter(_.id === id).delete) recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        0
    }

  def all(): Future[List[Food]] =
    database.run(Foods.to[List].result) recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        List.empty[Food]
    }

  def create(restaurant: Food): Future[Any] =
    database.run(Foods += restaurant) recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
    }

  def create(restaurants: List[Food]): Future[Option[Int]] =
    database.run(Foods ++= restaurants) recover {
      case NonFatal(x) if !x.getMessage.contains("Duplicate entry") =>
        logger.warn(x.getMessage)
        None
    }

  def insertOrUpdate(restaurant: Food): Future[Int] =
    database.run {
      Foods.insertOrUpdate(restaurant)
    } recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        0
    }
}

class FoodTable(tag: Tag) extends Table[Food](tag, "food") {
  def id           = column[Long]("id", O.PrimaryKey)
  def itemId       = column[Long]("item_id")
  def restaurantId = column[Long]("restaurant_id")
  def categoryId   = column[Long]("category_id")
  def name         = column[String]("name")
  def description  = column[String]("description")
  def monthSales   = column[Int]("month_sales")
  def rating       = column[Float]("rating")
  def ratingCount  = column[Int]("rating_count")
  def satisfyCount = column[Int]("satisfy_count")
  def satisfyRate  = column[Float]("satisfy_rate")

  def * =
    (id.?, itemId, restaurantId, categoryId, name, description, monthSales, rating, ratingCount, satisfyCount, satisfyRate) <> ((Food.apply _).tupled, Food.unapply)

}
