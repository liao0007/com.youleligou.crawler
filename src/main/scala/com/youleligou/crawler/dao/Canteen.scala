package dao

import com.youleligou.crawler.dao.schema.CanCan
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import slick.lifted.Tag
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Future

case class Canteen(
                    id: Long = 0,
                    address: String,
                    averageCost: String,
                    description: String,
                    distance: Int,
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

object Canteen {
  implicit val canteenReads: Reads[Canteen] = (
    (JsPath \ "id").read[Long] and
      (JsPath \ "address").read[String] and
      (JsPath \ "average_cost").read[String] and
      (JsPath \ "description").read[String] and
      (JsPath \ "distance").read[Int] and
      (JsPath \ "float_delivery_fee").read[Float] and
      (JsPath \ "float_minimum_order_amount").read[Float] and
      (JsPath \ "image_path").read[String] and
      (JsPath \ "is_new").read[Boolean] and
      (JsPath \ "is_premium").read[Boolean] and
      (JsPath \ "latitude").read[Float] and
      (JsPath \ "longitude").read[Float] and
      (JsPath \ "name").read[String] and
      (JsPath \ "phone").read[String] and
      (JsPath \ "promotion_info").read[String] and
      (JsPath \ "rating").read[Float] and
      (JsPath \ "rating_count").read[Int] and
      (JsPath \ "recent_order_num").read[Int] and
      (JsPath \ "status").read[Int]
    ) (Canteen.apply _)
}

class CanteenRepo extends CanCan {
  val Canteens: TableQuery[CanteenTable] = TableQuery[CanteenTable]

  def find(id: Long): Future[Option[Canteen]] = try {
    db.run(Canteens.filter(_.id === id).result.headOption)
  } finally db.close()

  def delete(id: Long): Future[Int] = try {
    db.run(Canteens.filter(_.id === id).delete)
  } finally db.close()

  def all(): Future[List[Canteen]] = try {
    db.run(Canteens.to[List].result)
  } finally db.close()

  def create(canteen: Canteen): Future[Long] = try {
    db.run(Canteens returning Canteens.map(_.id) += canteen)
  } finally db.close()

  def create(canteens: List[Canteen]): Future[Option[Int]] = try {
    db.run(Canteens ++= canteens)
  } finally db.close()
}

class CanteenTable(tag: Tag) extends Table[Canteen](tag, "canteen") {
  def id = column[Long]("id", O.PrimaryKey)

  def address = column[String]("address")

  def averageCost = column[String]("average_cost")

  def description = column[String]("description")

  def distance = column[Int]("distance")

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
      distance,
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
      status) <> ((Canteen.apply _).tupled, Canteen.unapply)

  //  def ? =
  //    (id.?, name.?).shaped.<>({ r =>
  //      import r._; _1.map(_ => Canteen.tupled((_1.get, _2.get)))
  //    }, (_: Any) => throw new Exception("Inserting into ? canteen not supported."))

}
