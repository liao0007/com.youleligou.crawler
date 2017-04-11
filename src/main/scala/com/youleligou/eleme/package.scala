package com.youleligou

import com.youleligou.crawler.actors.NamedActor

/**
  * Created by liangliao on 11/4/17.
  */
package object eleme {
  object RestaurantFetchActor extends NamedActor {
    final val name     = "ElemeRestaurantFetchActor"
    final val poolName = "ElemeRestaurantFetchActorPool"
  }

  object RestaurantInjectActor extends NamedActor {
    final val name     = "ElemeRestaurantInjectActor"
    final val poolName = "ElemeRestaurantInjectActorPool"
  }

  object RestaurantParseActor extends NamedActor {
    final val name     = "ElemeRestaurantParseActor"
    final val poolName = "ElemeRestaurantParseActorPool"
  }

}
