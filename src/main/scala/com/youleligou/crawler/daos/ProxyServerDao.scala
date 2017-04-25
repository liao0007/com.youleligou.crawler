package com.youleligou.crawler.daos

import com.youleligou.crawler.models.{Job, ProxyServer}
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
    lastVerifiedAt: Option[DateTime],
    checkCount: Int,
    createdAt: DateTime
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
    checkCount = model.checkCount,
    createdAt = model.createdAt
  )

  implicit def convertSeq(source: Seq[Job])(implicit converter: Job => ProxyServerDao): Seq[ProxyServerDao] = source map converter
}
