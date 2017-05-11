package com.youleligou.baidu.daos

import java.sql.Timestamp
import java.time.{LocalDate, LocalDateTime}
import java.util.Date

import com.youleligou.baidu.models._
import com.youleligou.core.daos.SnapshotDao

case class DishSnapshotDao(
    createdDate: java.util.Date = java.sql.Date.valueOf(LocalDate.now()),
    shopId: Long,
    categoryId: Long,
    itemId: Long,
    //PK
    name: String,
    url: String,
    purchaseLimit: Int,
    originPrice: Float, //string to float
    currentPrice: Float, //string to float
    saledOut: Boolean, //string to Boolean
    saled: Int,
    description: String,
    onSale: Boolean,
    recommendNum: Int,
    goodCommentNum: Int,
    badCommentNum: Int,
    totalCommentNum: Int,
    goodCommentRatio: Float, //string to float
    haveAttr: Boolean,
    dishType: Int,
    createdAt: Date = Timestamp.valueOf(LocalDateTime.now())
) extends SnapshotDao

object DishSnapshotDao {

  implicit def fromModel(model: Dish)(implicit categoryDao: CategoryDao): DishSnapshotDao = DishSnapshotDao(
    shopId = categoryDao.shopId,
    categoryId = model.categoryId,
    itemId = model.itemId,
    //PK
    name = model.name,
    url = model.url,
    purchaseLimit = model.purchaseLimit,
    originPrice = model.originPrice,
    currentPrice = model.currentPrice,
    saledOut = model.saledOut,
    saled = model.saled,
    description = model.description,
    onSale = model.onSale,
    recommendNum = model.recommendNum,
    goodCommentNum = model.goodCommentNum,
    badCommentNum = model.badCommentNum,
    totalCommentNum = model.totalCommentNum,
    goodCommentRatio = model.goodCommentRatio,
    haveAttr = model.haveAttr,
    dishType = model.dishType
  )
  implicit def fromModel(source: Seq[Dish])(implicit converter: Dish => DishSnapshotDao, categoryDao: CategoryDao): Seq[DishSnapshotDao] =
    source map converter

  implicit def toModel(dao: DishSnapshotDao): Dish = Dish(
    categoryId = dao.categoryId,
    itemId = dao.itemId,
    name = dao.name,
    url = dao.url,
    purchaseLimit = dao.purchaseLimit,
    originPrice = dao.originPrice,
    currentPrice = dao.currentPrice,
    saledOut = dao.saledOut,
    saled = dao.saled,
    description = dao.description,
    onSale = dao.onSale,
    recommendNum = dao.recommendNum,
    goodCommentNum = dao.goodCommentNum,
    badCommentNum = dao.badCommentNum,
    totalCommentNum = dao.totalCommentNum,
    goodCommentRatio = dao.goodCommentRatio,
    haveAttr = dao.haveAttr,
    dishType = dao.dishType,
    dishAttr = Seq.empty[DishAttribute]
  )
  implicit def toModel(source: Seq[DishSnapshotDao])(implicit converter: DishSnapshotDao => Dish): Seq[Dish] =
    source map converter

}
