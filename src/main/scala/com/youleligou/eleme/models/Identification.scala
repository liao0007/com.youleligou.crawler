package com.youleligou.eleme.models

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{JsPath, Json, OFormat, Reads}

/**
  * Created by liangliao on 23/4/17.
  */
case class Identification(licensesNumber: Option[String], companyName: Option[String])

object Identification {
  implicit val format: OFormat[Identification] = Json.format[Identification]
}
