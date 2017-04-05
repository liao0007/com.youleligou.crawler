package com.youleligou.crawler.daos.schema

import slick.jdbc.MySQLProfile
import slick.jdbc.MySQLProfile.api._

/**
  * Created by liangliao on 1/4/17.
  */
object CanCan {
  val db: MySQLProfile.backend.Database = Database.forConfig("db.cancan")
}
