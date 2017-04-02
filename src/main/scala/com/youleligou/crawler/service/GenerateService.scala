package com.youleligou.crawler.service

import com.youleligou.crawler.model.UrlInfo

trait GenerateService {
  def generate(seed: String): UrlInfo
}
