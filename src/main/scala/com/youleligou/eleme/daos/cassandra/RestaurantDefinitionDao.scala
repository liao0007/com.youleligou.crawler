package com.youleligou.eleme.daos.cassandra

import org.joda.time.DateTime

case class RestaurantDefinitionDao(
    id: Long,
    address: String,
    latitude: Float,
    longitude: Float,
    name: String,
    licensesNumber: Option[String] = None,
    companyName: Option[String] = None,
    createdAt: DateTime = DateTime.now()
)


