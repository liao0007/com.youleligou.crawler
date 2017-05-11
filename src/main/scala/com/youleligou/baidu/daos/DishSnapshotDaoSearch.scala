package com.youleligou.baidu.daos

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.{LocalDate, LocalDateTime}

import com.youleligou.baidu.models.DishAttribute
import com.youleligou.core.daos.SnapshotDao

import scala.util.Try

case class DishSnapshotDaoSearch(
    id: String,
    createdDate: java.util.Date = java.sql.Date.valueOf(LocalDate.now()),
    shopId: Long,
    categoryId: Long,
    itemId: Long,
    //PK
    name: String,
    url: String,
    purchaseLimit: Int,
    originPrice: Float,
    currentPrice: Float,
    saledOut: Boolean,
    saled: Int,
    description: String,
    onSale: Boolean,
    recommendNum: Int,
    goodCommentNum: Int,
    badCommentNum: Int,
    totalCommentNum: Int,
    goodCommentRatio: Float,
    haveAttr: Boolean,
    dishType: Int,
    shop: ShopDaoSearch,
    category: CategoryDaoSearch,
    dishAttributes: Seq[DishAttribute],
    createdAt: java.util.Date = Timestamp.valueOf(LocalDateTime.now())
) extends SnapshotDao

object DishSnapshotDaoSearch {
  implicit def fromDao(dao: DishSnapshotDao)(implicit shopDaoSearch: ShopDaoSearch,
                                             categoryDaoSearch: CategoryDaoSearch,
                                             dishAttributes: Seq[DishAttribute]): DishSnapshotDaoSearch = {
    val balancedPrice: Float =
      if (dao.haveAttr)
        Try(dishAttributes.map(_.price).sum / dishAttributes.length)
          .getOrElse(0f)
      else dao.currentPrice
    val formatter = new SimpleDateFormat("yyyy-MM-dd")

    DishSnapshotDaoSearch(
      id = s"${dao.itemId}-${formatter.format(dao.createdDate)}",
      createdDate = dao.createdDate,
      shopId = dao.shopId,
      categoryId = dao.categoryId,
      itemId = dao.itemId,
      //PK
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
      shop = shopDaoSearch,
      category = categoryDaoSearch,
      dishAttributes = dishAttributes,
      createdAt = dao.createdAt
    )
  }

  implicit def toDao(search: DishSnapshotDaoSearch): DishSnapshotDao = DishSnapshotDao(
    createdDate = search.createdDate,
    shopId = search.shopId,
    categoryId = search.categoryId,
    itemId = search.itemId,
    //PK
    name = search.name,
    url = search.url,
    purchaseLimit = search.purchaseLimit,
    originPrice = search.originPrice,
    currentPrice = search.currentPrice,
    saledOut = search.saledOut,
    saled = search.saled,
    description = search.description,
    onSale = search.onSale,
    recommendNum = search.recommendNum,
    goodCommentNum = search.goodCommentNum,
    badCommentNum = search.badCommentNum,
    totalCommentNum = search.totalCommentNum,
    goodCommentRatio = search.goodCommentRatio,
    haveAttr = search.haveAttr,
    dishType = search.dishType,
    createdAt = search.createdAt
  )
  implicit def toDao(source: Seq[DishSnapshotDaoSearch])(implicit converter: DishSnapshotDaoSearch => DishSnapshotDao): Seq[DishSnapshotDao] =
    source map converter

}
