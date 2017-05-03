package com.youleligou.eleme.daos

import com.youleligou.eleme.models.{Identification, Restaurant}

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
case class RestaurantSearchDao(
    id: Long,
    name: String,
    address: String,
    location: Map[String, Float],
    licensesNumber: Option[String] = None,
    companyName: Option[String] = None
)

object RestaurantSearchDao {
  implicit def fromModel(model: Restaurant): RestaurantSearchDao = RestaurantSearchDao(
    id = model.id,
    name = model.name,
    address = model.address,
    location = Map(
      "lat" -> model.latitude,
      "lon" -> model.longitude
    ),
    licensesNumber = model.identification.flatMap(_.licensesNumber),
    companyName = model.identification.flatMap(_.companyName)
  )

  implicit def toModel(dao: RestaurantSearchDao): Restaurant = Restaurant(
    id = dao.id,
    name = dao.name,
    address = dao.address,
    latitude = dao.location("lat"),
    longitude = dao.location("lon"),
    identification = Some(Identification(dao.licensesNumber, dao.companyName)),
    imagePath = ""
  )

  implicit def convertDaoSeq(source: Seq[Restaurant])(implicit converter: Restaurant => RestaurantSearchDao): Seq[RestaurantSearchDao] =
    source map converter

  implicit def convertModelSeq(source: Seq[RestaurantSearchDao])(implicit converter: RestaurantSearchDao => Restaurant): Seq[Restaurant] =
    source map converter
}
