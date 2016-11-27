package infrastructure.cassandra.table

import java.util.UUID

import com.websudos.phantom.dsl._

import domain.entities.Relative

class Relatives extends CassandraTable[Relatives, Relative] {

  override lazy val tableName = "relatives"

  object dashboard_id extends UUIDColumn(this) with PrimaryKey[UUID]
  object nsid extends StringColumn(this) with ClusteringOrder[String]
  object username extends StringColumn(this)
  object realname extends StringColumn(this)
  object followed extends BooleanColumn(this)
  object faved extends IntColumn(this)
  object faving extends IntColumn(this)
  object contacts extends IntColumn(this)
  object photos extends IntColumn(this)
  object avg_points extends DoubleColumn(this)
  object top_avg_points extends DoubleColumn(this)
  object top_tags extends ListColumn[Relatives, Relative, String](this)

  override def fromRow(r: Row) = Relative(nsid(r), username(r), realname(r), followed(r),
    faved(r), faving(r), contacts(r), photos(r), avg_points(r), top_avg_points(r), top_tags(r))

}

abstract class ConcreteRelatives extends Relatives with RootConnector {

  def insertRelative(dashboardId: UUID, relative: Relative) =
    insert()
      .value(_.dashboard_id, dashboardId)
      .value(_.nsid, relative.nsid)
      .value(_.username, relative.username)
      .value(_.realname, relative.realname)
      .value(_.followed, relative.followed)
      .value(_.faved, relative.faved)
      .value(_.faving, relative.faving)
      .value(_.contacts, relative.contacts)
      .value(_.photos, relative.photos)
      .value(_.avg_points, relative.avgPoints)
      .value(_.top_avg_points, relative.topAvgPoints)
      .value(_.top_tags, relative.topTags.toList)
      .future
      .map(_ => true)

  def getByDashboardId(dashboard_id: UUID) =
    select.
      where(_.dashboard_id eqs dashboard_id).
      fetch()

}

