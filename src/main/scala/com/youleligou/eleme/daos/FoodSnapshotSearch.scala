package com.youleligou.eleme.daos

import java.sql.Timestamp
import java.time.{LocalDate, LocalDateTime}

import com.youleligou.eleme.models.{Category, Food, Restaurant}

/*
class for elastic search

PUT eleme
{
    "settings" : {
        "number_of_shards" : 3
    },
    "mappings": {
      "restaurant": {
        "properties": {
          "address": {
            "type": "text",
            "fields": {
              "keyword": {
                "type": "keyword",
                "ignore_above": 256
              }
            }
          },
          "id": {
            "type": "long"
          },
          "location": {
            "type": "geo_point"
          },
          "name": {
            "type": "text",
            "fields": {
              "keyword": {
                "type": "keyword",
                "ignore_above": 256
              }
            }
          }
        }
      }
    }
}

 */

case class CategorySearch(
    id: Long,
    typ: Int,
    isActivity: Option[Boolean] = None,
    activity: Option[String] = None,
    description: String,
    iconUrl: String,
    name: String
)

object CategorySearch {
  implicit def fromModel(model: Category): CategorySearch = CategorySearch(
    id = model.id,
    typ = model.typ,
    isActivity = model.isActivity,
    activity = model.activity,
    description = model.description,
    iconUrl = model.iconUrl,
    name = model.name
  )

  implicit def toModel(dao: CategorySearch): Category = Category(
    id = dao.id,
    typ = dao.typ,
    isActivity = dao.isActivity,
    activity = dao.activity,
    description = dao.description,
    iconUrl = dao.iconUrl,
    name = dao.name,
    foods = Seq.empty[Food]
  )

  implicit def convertDaoSeq(source: Seq[Category])(implicit converter: Category => CategorySearch): Seq[CategorySearch] =
    source map converter

  implicit def convertModelSeq(source: Seq[CategorySearch])(implicit converter: CategorySearch => Category): Seq[Category] =
    source map converter
}

case class FoodSnapshotSearch(
    itemId: Long,
    name: String,
    restaurant: RestaurantSearch,
    category: CategorySearch,
    description: String,
    monthSales: Int,
    rating: Float,
    ratingCount: Int,
    satisfyCount: Int,
    satisfyRate: Float,
    createdDate: java.sql.Date = java.sql.Date.valueOf(LocalDate.now()),
    createdAt: java.util.Date = Timestamp.valueOf(LocalDateTime.now())
) {
  def id = s"$itemId - ${createdDate.toString}"
}

object FoodSnapshotSearch {
  implicit def fromModel(model: Food)(implicit restaurantModel: Restaurant, categoryModel: Category): FoodSnapshotSearch = FoodSnapshotSearch(
    itemId = model.itemId,
    name = model.name,
    restaurant = restaurantModel,
    category = categoryModel,
    description = model.description,
    monthSales = model.monthSales,
    rating = model.rating,
    ratingCount = model.ratingCount,
    satisfyCount = model.satisfyCount,
    satisfyRate = model.satisfyRate
  )

  implicit def toModel(dao: FoodSnapshotSearch): Food = Food(
    itemId = dao.itemId,
    name = dao.name,
    restaurantId = dao.restaurant.id,
    categoryId = dao.category.id,
    description = dao.description,
    monthSales = dao.monthSales,
    rating = dao.rating,
    ratingCount = dao.ratingCount,
    satisfyCount = dao.satisfyCount,
    satisfyRate = dao.satisfyRate
  )

  implicit def convertDaoSeq(source: Seq[Food])(implicit converter: Food => FoodSnapshotSearch): Seq[FoodSnapshotSearch] =
    source map converter

  implicit def convertModelSeq(source: Seq[FoodSnapshotSearch])(implicit converter: FoodSnapshotSearch => Food): Seq[Food] =
    source map converter
}
