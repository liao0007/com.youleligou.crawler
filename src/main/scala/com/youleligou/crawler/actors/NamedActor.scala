package com.youleligou.crawler.actors

/**
  * A convenience trait for an actor companion object to extend to provide names.
  */
trait NamedActor {
  val Name: String
  val PoolName: String
}
