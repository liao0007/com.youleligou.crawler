package com.youleligou.eleme.daos

import org.joda.time.DateTime

case class RestaurantDao(
    id: Long,
    name: String,
    address: String,
    imagePath: String,
    latitude: Float,
    longitude: Float,
    licensesNumber: Option[String] = None,
    companyName: Option[String] = None,
    createdAt: DateTime = DateTime.now()
)
