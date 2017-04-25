package com.youleligou.crawler.models

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.UUID

import org.joda.time.DateTime
import play.api.libs.json._

/**
  * Created by liangliao on 25/4/17.
  */
case class ProxyServer(
    ip: String,
    port: Int,
    username: Option[String] = None,
    password: Option[String] = None,
    isAnonymous: Option[Boolean] = None,
    supportedType: Option[String] = None,
    location: Option[String] = None,
    reactTime: Option[Float] = None,
    isLive: Boolean = false,
    checkCount: Int = 0,
    lastVerifiedAt: Option[DateTime] = None,
    createdAt: DateTime = DateTime.now()
)

object ProxyServer {
  implicit val jsonFormat: OFormat[ProxyServer] = Json.format[ProxyServer]

  //timestamp json formatter
  implicit object timestampFormat extends Format[Timestamp] {
    val format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS'Z'")

    def reads(json: JsValue) = {
      val str = json.as[String]
      JsSuccess(new Timestamp(format.parse(str).getTime))
    }

    def writes(ts: Timestamp) = JsString(format.format(ts))
  }

}
