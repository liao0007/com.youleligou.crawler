package com.youleligou.meituan.daos

import java.sql.Timestamp
import java.time.{LocalDate, LocalDateTime}

import com.youleligou.core.daos.SnapshotDao
import com.youleligou.meituan.modals.Sku

import scala.util.Try

/**
  * Created by liangliao on 8/5/17.
  */
case class SpuSnapshotDaoSearch( // food
                                 id: String,
                                 poiId: Long,
                                 tagId: Long,
                                 spuId: Long,
                                 name: String,
                                 minPrice: Float,
                                 praiseNum: Int,
                                 treadNum: Int,
                                 praiseNumNew: Int,
                                 description: Option[String],
                                 picture: String,
                                 monthRevenue: Float,
                                 monthSaled: Int,
                                 balancedPrice: Float,
                                 status: Int,
                                 poi: PoiDaoSearch,
                                 foodTag: FoodTagDaoSearch,
                                 skus: Seq[Sku],
                                 createdDate: java.util.Date = java.sql.Date.valueOf(LocalDate.now()),
                                 createdAt: java.util.Date = Timestamp.valueOf(LocalDateTime.now()))
    extends SnapshotDao

object SpuSnapshotDaoSearch {
  implicit def fromDao(
      dao: SpuSnapshotDao)(implicit poiDaoSearch: PoiDaoSearch, tagDaoSearch: FoodTagDaoSearch, skus: Seq[Sku]): SpuSnapshotDaoSearch = {
    val balancedPrice: Float = Try(skus.map(_.price).sum / skus.length).getOrElse(0f)
    SpuSnapshotDaoSearch(
      id = s"${dao.spuId}-${dao.createdDate}",
      poiId = poiDaoSearch.poiId,
      tagId = tagDaoSearch.tagId,
      spuId = dao.spuId,
      name = dao.name,
      minPrice = dao.minPrice,
      praiseNum = dao.praiseNum,
      treadNum = dao.treadNum,
      praiseNumNew = dao.praiseNumNew,
      description = dao.description,
      picture = dao.picture,
      monthRevenue = dao.monthSaled * balancedPrice,
      monthSaled = dao.monthSaled,
      balancedPrice = balancedPrice,
      status = dao.status,
      poi = poiDaoSearch,
      foodTag = tagDaoSearch,
      skus = skus,
      createdDate = dao.createdDate,
      createdAt = dao.createdAt
    )
  }

  implicit def toDao(search: SpuSnapshotDaoSearch): SpuSnapshotDao = SpuSnapshotDao(
    poiId = search.poiId,
    tagId = search.tagId,
    spuId = search.spuId,
    name = search.name,
    minPrice = search.minPrice,
    praiseNum = search.praiseNum,
    treadNum = search.treadNum,
    praiseNumNew = search.praiseNumNew,
    description = search.description,
    picture = search.picture,
    monthSaled = search.monthSaled,
    status = search.status,
    createdDate = search.createdDate,
    createdAt = search.createdAt
  )
  implicit def toDao(source: Seq[SpuSnapshotDaoSearch])(implicit converter: SpuSnapshotDaoSearch => SpuSnapshotDao): Seq[SpuSnapshotDao] =
    source map converter

}
