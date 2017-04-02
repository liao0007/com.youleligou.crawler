package com.youleligou.eleme.dao

import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.dao.schema.CanCan
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import slick.jdbc.MySQLProfile.api._
import slick.lifted.Tag

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._

case class Restaurant(
                       id: Long = 0,
                       address: String,
                       averageCost: String,
                       description: String,
                       deliveryFee: Float,
                       minimumOrderAmount: Float,
                       imagePath: String,
                       isNew: Boolean,
                       isPremium: Boolean,
                       latitude: Float,
                       longitude: Float,
                       name: String,
                       phone: String,
                       promotionInfo: String,
                       rating: Float,
                       ratingCount: Int,
                       recentOrderNum: Int,
                       status: Int
                     )

object Restaurant {
  implicit val restaurantReads: Reads[Restaurant] = (
    (JsPath \ "id").read[Long] and
      (JsPath \ "address").readWithDefault[String]("").map(_.trim) and
      (JsPath \ "average_cost").readWithDefault[String]("").map(_.trim) and
      (JsPath \ "description").readWithDefault[String]("").map(_.trim) and
      (JsPath \ "float_delivery_fee").readWithDefault[Float](0f) and
      (JsPath \ "float_minimum_order_amount").readWithDefault[Float](0f) and
      (JsPath \ "image_path").readWithDefault[String]("").map(_.trim) and
      (JsPath \ "is_new").readWithDefault[Boolean](false) and
      (JsPath \ "is_premium").readWithDefault[Boolean](false) and
      (JsPath \ "latitude").readWithDefault[Float](0f) and
      (JsPath \ "longitude").readWithDefault[Float](0f) and
      (JsPath \ "name").readWithDefault[String]("").map(_.trim) and
      (JsPath \ "phone").readWithDefault[String]("").map(_.trim) and
      (JsPath \ "promotion_info").readWithDefault[String]("").map(_.trim) and
      (JsPath \ "rating").readWithDefault[Float](0f) and
      (JsPath \ "rating_count").readWithDefault[Int](0) and
      (JsPath \ "recent_order_num").readWithDefault[Int](0) and
      (JsPath \ "status").readWithDefault[Int](0)
    ) (Restaurant.apply _)
}

class RestaurantRepo extends LazyLogging {
  val Restaurants: TableQuery[RestaurantTable] = TableQuery[RestaurantTable]

  def find(id: Long): Future[Option[Restaurant]] =
    CanCan.db.run(Restaurants.filter(_.id === id).result.headOption)

  def delete(id: Long): Future[Int] =
    CanCan.db.run(Restaurants.filter(_.id === id).delete)

  def all(): Future[List[Restaurant]] =
    CanCan.db.run(Restaurants.to[List].result)

  def create(restaurant: Restaurant): Future[Any] =
    CanCan.db.run(Restaurants += restaurant).recover {
      case t: Throwable =>
        logger.error(t.getMessage)
    }

  def create(restaurants: List[Restaurant]): Future[Option[Int]] =
    CanCan.db.run(Restaurants ++= restaurants)
}

class RestaurantTable(tag: Tag) extends Table[Restaurant](tag, "restaurant") {
  def id = column[Long]("id", O.PrimaryKey)

  def address = column[String]("address")

  def averageCost = column[String]("average_cost")

  def description = column[String]("description")

  def deliveryFee = column[Float]("delivery_fee")

  def minimumOrderAmount = column[Float]("minimum_order_amount")

  def imagePath = column[String]("image_path")

  def isNew = column[Boolean]("is_new")

  def isPremium = column[Boolean]("is_premium")

  def latitude = column[Float]("latitude")

  def longitude = column[Float]("longitude")

  def name = column[String]("name")

  def phone = column[String]("phone")

  def promotionInfo = column[String]("promotion_info")

  def rating = column[Float]("rating")

  def ratingCount = column[Int]("rating_count")

  def recentOrderNum = column[Int]("recent_order_num")

  def status = column[Int]("status")

  def * =
    (id,
      address,
      averageCost,
      description,
      deliveryFee,
      minimumOrderAmount,
      imagePath,
      isNew,
      isPremium,
      latitude,
      longitude,
      name,
      phone,
      promotionInfo,
      rating,
      ratingCount,
      recentOrderNum,
      status) <> ((Restaurant.apply _).tupled, Restaurant.unapply)

}
