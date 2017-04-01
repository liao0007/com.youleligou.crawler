package com.youleligou.crawler.dao.schema

import slick.jdbc.MySQLProfile
import slick.jdbc.MySQLProfile.api._

/**
  * Created by liangliao on 1/4/17.
  */
trait CanCan {
  def db: MySQLProfile.backend.Database = Database.forConfig("db.cancan")
}
