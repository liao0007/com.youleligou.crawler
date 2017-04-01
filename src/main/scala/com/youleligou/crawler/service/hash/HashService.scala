package com.youleligou.crawler.service.hash

/**
  * Created by liangliao on 1/4/17.
  */
trait HashService {
  def hash(text: String): String
}
