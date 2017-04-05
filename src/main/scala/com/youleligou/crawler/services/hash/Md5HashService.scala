package com.youleligou.crawler.services.hash

import com.youleligou.crawler.services.HashService

/**
  * Created by liangliao on 1/4/17.
  */
class Md5HashService extends HashService {
  def hash(text: String): String =
    java.security.MessageDigest.getInstance("MD5").digest(text.getBytes()).map(0xFF & _).map {
      "%02x".format(_)
    }.foldLeft("") {
      _ + _
    }
}
