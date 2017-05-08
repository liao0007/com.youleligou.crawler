package com.youleligou.meituan.daos

import java.sql.Timestamp
import java.time.{LocalDate, LocalDateTime}

import com.youleligou.core.daos.SnapshotDao
import com.youleligou.meituan.modals.Sku

/**
  * Created by liangliao on 8/5/17.
  */
case class SpuSnapshotDaoSearch( // food
                                id: String,
                                spuId: Long,
                                name: String,
                                minPrice: Float,
                                priseNum: Int,
                                treadNum: Int,
                                priseNumNew: Int,
                                description: Option[String],
                                picture: String,
                                monthSaled: Int,
                                balancedPrice: Float,
                                status: Int,
                                tag: Long,
                                poi: PoiDaoSearch,
                                foodTag: FoodTagDaoSearch,
                                skus: Seq[Sku],
                                createdDate: java.util.Date = java.sql.Date.valueOf(LocalDate.now()),
                                createdAt: java.util.Date = Timestamp.valueOf(LocalDateTime.now()))
    extends SnapshotDao

object SpuSnapshotDaoSearch {
  implicit def fromDao(
      dao: SpuSnapshotDao)(implicit pioDaoSearch: PoiDaoSearch, tagDaoSearch: FoodTagDaoSearch, skus: Seq[Sku]): SpuSnapshotDaoSearch =
    SpuSnapshotDaoSearch(
      id = s"${dao.id}-${dao.createdDate}",
      spuId = dao.id,
      name = dao.name,
      minPrice = dao.minPrice,
      priseNum = dao.priseNum,
      treadNum = dao.treadNum,
      priseNumNew = dao.priseNumNew,
      description = dao.description,
      picture = dao.picture,
      monthSaled = dao.monthSaled,
      balancedPrice = skus.map(_.price).sum / skus.length,
      status = dao.status,
      tag = dao.tag,
      poi = pioDaoSearch,
      foodTag = tagDaoSearch,
      skus = skus,
      createdDate = dao.createdDate,
      createdAt = dao.createdAt
    )

  implicit def toDao(search: SpuSnapshotDaoSearch): SpuSnapshotDao = SpuSnapshotDao(
    id = search.spuId,
    name = search.name,
    minPrice = search.minPrice,
    priseNum = search.priseNum,
    treadNum = search.treadNum,
    priseNumNew = search.priseNumNew,
    description = search.description,
    picture = search.picture,
    monthSaled = search.monthSaled,
    status = search.status,
    tag = search.tag,
    createdDate = search.createdDate,
    createdAt = search.createdAt
  )
  implicit def toDao(source: Seq[SpuSnapshotDaoSearch])(implicit converter: SpuSnapshotDaoSearch => SpuSnapshotDao): Seq[SpuSnapshotDao] =
    source map converter

}
