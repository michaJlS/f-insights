package infrastructure.cassandra.table

import com.websudos.phantom.dsl._
import java.util.UUID
import models.flickr.Dashboard

sealed class Dashboards extends CassandraTable[Dashboards, Dashboard]
{

  override lazy val tableName = "dashboards"

  object nsid extends StringColumn(this) with PartitionKey[String]
  object dashboard_id extends UUIDColumn(this) with ClusteringOrder[UUID]
  object created_at extends DateTimeColumn(this)

  override def fromRow(r: Row): Dashboard = {
    Dashboard(nsid(r), dashboard_id(r), created_at(r))
  }

}

abstract class ConcreteDashboards extends Dashboards with RootConnector
{

  def insertDashboard(dashboard: Dashboard) = {
    insert.
      value(_.nsid, dashboard.nsid).
      value(_.dashboard_id, dashboard.id).
      value(_.created_at, dashboard.created_at).
      future()
  }

  def getById(nsid: String, dashboard_id: UUID) = {
    select.
      where(_.nsid eqs nsid).
      and(_.dashboard_id eqs dashboard_id).
      one()
  }

}
