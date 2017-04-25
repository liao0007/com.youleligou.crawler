package com.youleligou.crawler.daos

import com.youleligou.crawler.models.{Job, ProxyServer}
import org.joda.time.DateTime

case class ProxyServerDao(
    ip: String,
    port: Int,
    username: Option[String] = None,
    password: Option[String] = None,
    isAnonymous: Option[Boolean] = None,
    supportedType: Option[String] = None,
    location: Option[String] = None,
    reactTime: Option[Float] = None,
    isLive: Boolean = false,
    lastVerifiedAt: Option[DateTime] = None,
    checkCount: Int = 0,
    createdAt: DateTime = DateTime.now()
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
