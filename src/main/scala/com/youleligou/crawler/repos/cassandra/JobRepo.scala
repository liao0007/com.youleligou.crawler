package com.youleligou.crawler.repos.cassandra

import com.google.inject.Inject
import com.youleligou.core.reps.CassandraRepo
import com.youleligou.crawler.daos.JobDao
import org.apache.spark.SparkContext

/**
  * Created by liangliao on 25/4/17.
  */
class JobRepo @Inject()(val keyspace: String = "crawler", val table: String = "jobs", val sparkContext: SparkContext)
    extends CassandraRepo[JobDao]
