package com.youleligou.crawler.services

import com.youleligou.crawler.actors.FetchActor.Fetch

import scala.concurrent.Future

trait InjectService {
  def initSeed(): Future[Int]

  def generateFetch(seed: Int): Fetch
}
