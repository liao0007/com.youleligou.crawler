package com.youleligou.eleme.daos

import com.youleligou.eleme.models.{Identification, Restaurant}

/*
class for elastic search

PUT eleme
{
  "eleme": {
    "aliases": {},
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
          "identification": {
            "properties": {
              "companyName": {
                "type": "text",
                "fields": {
                  "keyword": {
                    "type": "keyword",
                    "ignore_above": 256
                  }
                }
              },
              "licensesNumber": {
                "type": "text",
                "fields": {
                  "keyword": {
                    "type": "keyword",
                    "ignore_above": 256
                  }
                }
              }
            }
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
    },
    "settings": {
      "index": {
        "number_of_shards": "3",
        "number_of_replicas": "1",
      }
    }
  }
}

 */
case class RestaurantSearch(
    id: Long,
    name: String,
    address: String,
    location: Map[String, Float],
    identification: Option[Identification]
)

object RestaurantSearch {
  implicit def fromModel(model: Restaurant): RestaurantSearch = RestaurantSearch(
    id = model.id,
    name = model.name,
    address = model.address,
    location = Map(
      "lat" -> model.latitude,
      "lon" -> model.longitude
    ),
    identification = model.identification
  )

  implicit def toModel(dao: RestaurantSearch): Restaurant = Restaurant(
    id = dao.id,
    address = dao.address,
    averageCost = None,
    description = "",
    deliveryFee = 0,
    minimumOrderAmount = 0,
    imagePath = "",
    isNew = false,
    isPremium = false,
    latitude = dao.location("lat"),
    longitude = dao.location("lon"),
    name = dao.name,
    phone = None,
    promotionInfo = "",
    rating = 0,
    ratingCount = 0,
    recentOrderNum = 0,
    identification = dao.identification,
    status = 0
  )

  implicit def convertDaoSeq(source: Seq[Restaurant])(implicit converter: Restaurant => RestaurantSearch): Seq[RestaurantSearch] =
    source map converter

  implicit def convertModelSeq(source: Seq[RestaurantSearch])(implicit converter: RestaurantSearch => Restaurant): Seq[Restaurant] =
    source map converter
}
