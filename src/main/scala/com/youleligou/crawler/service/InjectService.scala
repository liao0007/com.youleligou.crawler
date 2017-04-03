package com.youleligou.crawler.service

import com.youleligou.crawler.actor.FetchActor.Fetch

import scala.concurrent.Future

trait InjectService {
  def initSeed(): Future[Int]

  def generateFetch(seed: Int): Fetch
}
