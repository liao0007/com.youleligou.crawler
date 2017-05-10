package com.youleligou.meituan.daos

import java.sql.Timestamp
import java.time.LocalDateTime

import com.youleligou.core.daos.Dao

/**
  * Created by liangliao on 8/5/17.
  */
case class PoiDaoSearch( // restaurant
                         poiId: Long,
                         mtPoiId: Long,
                         name: String,
                         status: Int,
                         picUrl: String,
                         brandType: Int,
                         location: Map[String, Float],
                         wmPoiViewId: Long,
                         createdAt: java.util.Date = Timestamp.valueOf(LocalDateTime.now()))
    extends Dao

object PoiDaoSearch {

  implicit def fromDao(dao: PoiDao): PoiDaoSearch = PoiDaoSearch(
    poiId = dao.poiId,
    mtPoiId = dao.mtPoiId,
    name = dao.name,
    status = dao.status,
    picUrl = dao.picUrl,
    brandType = dao.brandType,
    location = Map(
      "lat" -> dao.latitude,
      "lon" -> dao.longitude
    ),
    wmPoiViewId = dao.wmPoiViewId,
    createdAt = dao.createdAt
  )
  implicit def fromDao(source: Seq[PoiDao])(implicit converter: PoiDao => PoiDaoSearch): Seq[PoiDaoSearch] =
    source map converter

  implicit def toDao(search: PoiDaoSearch): PoiDao = PoiDao(
    poiId = search.poiId,
    mtPoiId = search.mtPoiId,
    name = search.name,
    status = search.status,
    picUrl = search.picUrl,
    brandType = search.brandType,
    latitude = search.location("lat"),
    longitude = search.location("log"),
    wmPoiViewId = search.wmPoiViewId,
    createdAt = search.createdAt
  )
  implicit def toDao(source: Seq[PoiDaoSearch])(implicit converter: PoiDaoSearch => PoiDao): Seq[PoiDao] =
    source map converter

}
