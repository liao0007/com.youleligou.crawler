package com.youleligou.crawler

import com.google.inject._
import com.youleligou.core.reps.Repo
import com.youleligou.crawler.daos.{JobDao, ProxyServerDao}
import com.youleligou.crawler.modules._
import com.youleligou.crawler.repos._
import net.codingwell.scalaguice.ScalaModule

/**
  * Created by liangliao on 31/3/17.
  */
class CrawlerModule extends AbstractModule with ScalaModule with GuiceAkkaActorRefProvider {
  override def configure() {
    bind[Repo[JobDao]].to[cassandra.JobRepo]
    bind[Repo[ProxyServerDao]].to[cassandra.ProxyServerRepo]
  }
}
