package com.youleligou.crawler.daos

import java.sql.Timestamp

import com.youleligou.crawler.models.ProxyServer
import org.joda.time.DateTime

case class ProxyServerDao(
    ip: String,
    port: Int,
    username: Option[String],
    password: Option[String],
    isAnonymous: Option[Boolean],
    supportedType: Option[String],
    location: Option[String],
    reactTime: Option[Float],
    isLive: Boolean,
    checkCount: Int,
    lastVerifiedAt: Option[Timestamp],
    createdAt: Timestamp = new Timestamp(DateTime.now().getMillis)
)

object ProxyServerDao {
  object JobType extends Enumeration {
    val Fetch = Value
  }

  implicit def fromModel(model: ProxyServer): ProxyServerDao = ProxyServerDao(
    ip = model.ip,
    port = model.port,
    username = model.username,
    password = model.password,
    isAnonymous = model.isAnonymous,
    supportedType = model.supportedType,
    location = model.location,
    reactTime = model.reactTime,
    isLive = model.isLive,
    lastVerifiedAt = model.lastVerifiedAt,
    checkCount = model.checkCount
  )

  implicit def convertSeq(source: Seq[ProxyServer])(implicit converter: ProxyServer => ProxyServerDao): Seq[ProxyServerDao] = source map converter
}
