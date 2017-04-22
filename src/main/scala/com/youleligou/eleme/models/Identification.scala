package com.youleligou.eleme.models

import play.api.libs.json.{Json, OFormat}

/**
  * Created by liangliao on 23/4/17.
  */
case class Identification(licensesNumber: Option[String], companyName: Option[String])

object Identification {
  implicit val format: OFormat[Identification] = Json.format[Identification]
}
