package infrastructure.cassandra

import com.websudos.phantom.dsl.{Database, KeySpaceDef}
import infrastructure.cassandra.table._


class FlickrAssistantDb(val keyspace:KeySpaceDef) extends Database(keyspace)
{

  object Dashboards extends ConcreteDashboards with keyspace.Connector
  object Favourites extends ConcreteFavourites with keyspace.Connector
  object AppUserDetails extends ConcreteAppUserDetails with keyspace.Connector

}
