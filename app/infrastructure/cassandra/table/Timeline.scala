package infrastructure.cassandra.table
import java.util.UUID

import com.websudos.phantom.dsl._

import domain.entities.MonthlyStats
class Timeline extends CassandraTable[Timeline, MonthlyStats] {

  override lazy val tableName = "timeline"

  object dashboard_id extends UUIDColumn(this) with PrimaryKey[UUID]
  object month extends StringColumn(this) with ClusteringOrder[String]
  object uploaded extends IntColumn(this)
  object faved extends IntColumn(this)
  object got_favs extends IntColumn(this)

  override def fromRow(r: Row) = MonthlyStats(month(r), uploaded(r), faved(r), got_favs(r))

}

abstract class ConcreteTimeline extends Timeline with RootConnector {

  def insertTimelineItem(dashboardId: UUID, monthly: MonthlyStats) =
    insert()
      .value(_.dashboard_id, dashboardId)
      .value(_.month, monthly.month)
      .value(_.uploaded, monthly.uploaded)
      .value(_.faved, monthly.faved)
      .value(_.got_favs, monthly.gotFavs)
      .future
      .map(_ => true)

  def getByDashboardId(dashboard_id: UUID) =
    select
      .where(_.dashboard_id eqs dashboard_id)
      .orderBy(_.month.desc)
      .fetch()

}