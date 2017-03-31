package com.youleligou.crawler.utils

/**
  * Created by dell on 2016/8/31.
  */
private[crawler] object JsonUtil {

  private val mapper = new ObjectMapper

  def toJson(obj: Any): String = {
    mapper.writeValueAsString(obj)
  }
}
