package com.youleligou.proxyHunters

import com.youleligou.crawler.actors.NamedActor

/**
  * Created by liangliao on 11/4/17.
  */
package object xicidaili {

  object ProxyListInjectActor extends NamedActor {
    final val name     = "XiCiDaiLiProxyListInjectActor"
    final val poolName = "XiCiDaiLiProxyListInjectActorPool"
  }

  object ProxyListFetchActor extends NamedActor {
    final val name     = "XiCiDaiLiProxyListFetchActor"
    final val poolName = "XiCiDaiLiProxyListFetchActorPool"
  }

  object ProxyListParseActor extends NamedActor {
    final val name     = "XiCiDaiLiProxyListParseActor"
    final val poolName = "XiCiDaiLiProxyListParseActorPool"
  }

}
