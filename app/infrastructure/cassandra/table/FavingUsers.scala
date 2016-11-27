package infrastructure.cassandra.table
import java.util.UUID

import com.websudos.phantom.dsl._

import domain.entities.FavingUserStats

class FavingUsers extends CassandraTable[FavingUsers, FavingUserStats] {

  override lazy val tableName = "faving_users"

  object dashboard_id extends UUIDColumn(this) with PrimaryKey[UUID]
  object user extends StringColumn(this) with ClusteringOrder[String]
  object username extends StringColumn(this)
  object realname extends StringColumn(this)
  object count extends IntColumn(this) with ClusteringOrder[Int]
  object first_fav extends StringColumn(this)
  object last_fav extends StringColumn(this)

  override def fromRow(r: Row) = FavingUserStats(user(r), username(r), realname(r), count(r), first_fav(r), last_fav(r))

}

abstract class ConcreteFavingUsers extends FavingUsers with RootConnector {

  def insertFavingUser(dashboardId: UUID, user: FavingUserStats) =
    insert()
      .value(_.dashboard_id, dashboardId)
      .value(_.user, user.user)
      .value(_.username, user.username)
      .value(_.realname, user.realname)
      .value(_.count, user.count)
      .value(_.first_fav, user.firstFav)
      .value(_.last_fav, user.lastFav)
      .future()
        .map(_ => true)


  def getByDashboardId(dashboard_id: UUID) =
    select
      .where(_.dashboard_id eqs dashboard_id)
      .fetch()
      .map(_.sortBy(_.count * -1))
      // workaround for "Order by currently only support the ordering of columns following their declared order in the PRIMARY KEY"

}