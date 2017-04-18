package com.youleligou.eleme.daos.cassandra

import com.google.inject.Inject
import com.google.inject.name.Named
import com.outworkers.phantom.connectors.CassandraConnection
import com.outworkers.phantom.dsl._

/**
  * Created by liangliao on 18/4/17.
  */
class ElemeDatabase @Inject()(@Named(keyspaces.Eleme) cassandraConnection: CassandraConnection)
    extends Database[ElemeDatabase](cassandraConnection) {
  object restaurants extends Restaurants with Connector
  object foods       extends Foods with Connector
}
