package com.youleligou.crawler.actors

/**
  * A convenience trait for an actor companion object to extend to provide names.
  */
trait NamedActor {
  val name: String
  val poolName: String
}
