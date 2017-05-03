package com.youleligou.eleme.models


/**
  * Created by liangliao on 2/5/17.
  */
case class Restaurant(
    id: Long,
    name: String,
    address: String,
    imagePath: String,
    latitude: Float,
    longitude: Float,
    identification: Option[Identification] = None
)
