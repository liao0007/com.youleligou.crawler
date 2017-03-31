package com.youleligou.crawler

import com.google.inject.Guice
import com.youleligou.modules.{AkkaModule, ApplicationModule, ConfigModule}

/**
  * Created by liangliao on 31/3/17.
  */
object Main {
  Guice.createInjector(
    new ConfigModule(),
    new AkkaModule(),
    new ApplicationModule
  )

}
