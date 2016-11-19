package infrastructure.cassandra.table

import com.websudos.phantom.dsl._
import java.util.UUID

import domain.entities.{Favourite, PhotoFavourite}

class PhotoFavourites extends CassandraTable[PhotoFavourites, PhotoFavourite]{

  override lazy val tableName = "favourites"

  object dashboard_id extends UUIDColumn(this) with PrimaryKey[UUID]
  object photo_id extends StringColumn(this) with ClusteringOrder[String]
  object owner extends StringColumn(this)
  object faved_by extends StringColumn(this) with ClusteringOrder[String]
  object username extends StringColumn(this)
  object realname extends StringColumn(this)
  object date_faved extends StringColumn(this)


  override def fromRow(r: Row) = PhotoFavourite(photo_id(r), owner(r), faved_by(r), username(r), realname(r), date_faved(r))

}

abstract class ConcretePhotoFavourites extends PhotoFavourites with RootConnector  {

  def insertPhotoFavourite(dashboard_id: UUID, photoFav: PhotoFavourite) = {
    insert.
      value(_.dashboard_id, dashboard_id).
      value(_.photo_id, photoFav.photoId).
      value(_.owner, photoFav.owner).
      value(_.faved_by, photoFav.faved_by).
      value(_.username, photoFav.username).
      value(_.realname, photoFav.realname).
      value(_.date_faved, photoFav.date_faved).
      future().
      map(_ => true)

  }

  def getByDashboardId(dashboard_id: UUID) =
    select.
      where(_.dashboard_id eqs dashboard_id).
      fetch()

}
