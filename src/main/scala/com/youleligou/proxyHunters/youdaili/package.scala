package com.youleligou.proxyHunters

import com.youleligou.crawler.actors.NamedActor

/**
  * Created by liangliao on 11/4/17.
  */
package object youdaili {
  object ProxyListInjectActor extends NamedActor {
    final val name     = "YouDaiLiProxyListInjectActor"
    final val poolName = "YouDaiLiProxyListInjectActorPool"
  }

  object ProxyListFetchActor extends NamedActor {
    final val name     = "YouDaiLiProxyListFetchActor"
    final val poolName = "YouDaiLiProxyListFetchActorPool"
  }

  object ProxyListParseActor extends NamedActor {
    final val name     = "YouDaiLiProxyListParseActor"
    final val poolName = "YouDaiLiProxyListParseActorPool"
  }

  object ProxyPageInjectActor extends NamedActor {
    final val name     = "YouDaiLiProxyPageInjectActor"
    final val poolName = "YouDaiLiProxyPageInjectActorPool"
  }

  object ProxyPageFetchActor extends NamedActor {
    final val name     = "YouDaiLiProxyPageFetchActor"
    final val poolName = "YouDaiLiProxyPageFetchActorPool"
  }

  object ProxyPageParseActor extends NamedActor {
    final val name     = "YouDaiLiProxyPageParseActor"
    final val poolName = "YouDaiLiProxyPageParseActorPool"
  }
}
