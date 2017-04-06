package com.youleligou.crawler.services

import com.typesafe.scalalogging.LazyLogging

/**
  * Created by liangliao on 1/4/17.
  */
trait HashService extends LazyLogging {
  def hash(text: String): String
}
