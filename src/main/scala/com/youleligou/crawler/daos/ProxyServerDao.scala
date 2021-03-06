package com.youleligou.crawler.daos

import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.Date

import com.youleligou.core.daos.Dao
import com.youleligou.crawler.models.ProxyServer

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
    lastVerifiedAt: Option[Date],
    createdAt: Date = Timestamp.valueOf(LocalDateTime.now())
) extends Dao

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
