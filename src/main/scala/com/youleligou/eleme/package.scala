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

  object FoodFetchActor extends NamedActor {
    final val name     = "ElemeFoodFetchActor"
    final val poolName = "ElemeFoodFetchActorPool"
  }
  object FoodInjectActor extends NamedActor {
    final val name     = "ElemeFoodInjectActor"
    final val poolName = "ElemeFoodInjectActorPool"
  }
  object FoodParseActor extends NamedActor {
    final val name     = "ElemeFoodParseActor"
    final val poolName = "ElemeFoodParseActorPool"
  }

}
