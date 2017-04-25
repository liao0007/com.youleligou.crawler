package com.youleligou.eleme.daos

import org.joda.time.DateTime

case class RestaurantDao(
    id: Long,
    address: String,
    latitude: Float,
    longitude: Float,
    name: String,
    licensesNumber: Option[String] = None,
    companyName: Option[String] = None,
    createdAt: DateTime = DateTime.now()
)
