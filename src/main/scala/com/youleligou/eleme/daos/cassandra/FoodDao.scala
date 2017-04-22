package com.youleligou.eleme.daos.cassandra

import com.outworkers.phantom.dsl._
import com.youleligou.eleme.models.Food
import org.joda.time.DateTime

import scala.concurrent.Future

case class FoodDao(
    itemId: Long,
    restaurantId: Long,
    categoryId: Long,
    name: String,
    description: String,
    monthSales: Int,
    rating: Float,
    ratingCount: Int,
    satisfyCount: Int,
    satisfyRate: Float,
    createdAt: DateTime = DateTime.now()
)

object FoodDao {

  implicit def fromModel(model: com.youleligou.eleme.models.Food): FoodDao = FoodDao(
    itemId = model.itemId,
    restaurantId = model.restaurantId,
    categoryId = model.categoryId,
    name = model.name,
    description = model.description,
    monthSales = model.monthSales,
    rating = model.rating,
    ratingCount = model.ratingCount,
    satisfyCount = model.satisfyCount,
    satisfyRate = model.satisfyRate
  )

  implicit def convertSeq(source: Seq[Food])(implicit converter: Food => FoodDao): Seq[FoodDao] = source map converter

}
abstract class Foods extends CassandraTable[Foods, FoodDao] with RootConnector {
  object restaurantId extends LongColumn(this) with PartitionKey
  object itemId       extends LongColumn(this)
  object categoryId   extends LongColumn(this)
  object name         extends StringColumn(this)
  object description  extends StringColumn(this)
  object monthSales   extends IntColumn(this)
  object rating       extends FloatColumn(this)
  object ratingCount  extends IntColumn(this)
  object satisfyCount extends IntColumn(this)
  object satisfyRate  extends FloatColumn(this)
  object createdAt    extends DateTimeColumn(this) with ClusteringOrder with Descending

  def batchInsertOrUpdate(foods: Seq[FoodDao]): Future[ResultSet] =
    Batch.unlogged
      .add(foods.map { food =>
        store(food)
      }.iterator)
      .future()

  def insertOrUpdate(foods: Seq[FoodDao]): Seq[Future[ResultSet]] = foods.map(insertOrUpdate)

  def insertOrUpdate(food: FoodDao): Future[ResultSet] = store(food).future()

  def all(): Future[List[FoodDao]] = select.fetch()
}
