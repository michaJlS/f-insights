package infrastructure.cassandra.table

import com.websudos.phantom.dsl._
import java.util.UUID
import models.flickr.{Favourite, PhotoExcerpt, PhotoUrls}


class Favourites extends CassandraTable[Favourites, Favourite]
{

  override lazy val tableName = "favourites"

  object dashboard_id extends UUIDColumn(this) with PrimaryKey[UUID]
  object photo_id extends StringColumn(this) with ClusteringOrder[String]
  object date_faved extends StringColumn(this)
  object faved_by extends StringColumn(this)
  object title extends StringColumn(this)
  object owner extends StringColumn(this)
  object owner_name extends StringColumn(this)
  object date_upload extends StringColumn(this)
  object date_taken extends StringColumn(this)
  object count_views extends IntColumn(this)
  object count_faves extends IntColumn(this)
  object count_comments extends IntColumn(this)
  object tags extends StringColumn(this)
  object machine_tags extends StringColumn(this)
  object urls extends MapColumn[Favourites, Favourite, String, String](this) //   urls frozen <photo_urls>,

  override def fromRow(r: Row) = Favourite(PhotoExcerpt(photo_id(r), title(r), owner(r), owner_name(r), date_upload(r), date_taken(r),
      count_views(r), count_faves(r), count_comments(r), tags(r), machine_tags(r), PhotoUrls(urls(r))
    ), faved_by(r), date_faved(r))

}


abstract class ConcreteFavourites extends Favourites with RootConnector
{

  def insertFavourite(dashboard_id: UUID, fav: Favourite) = {
    insert.
      value(_.dashboard_id, dashboard_id).
      value(_.photo_id, fav.photo.id).
      value(_.faved_by, fav.faved_by).
      value(_.date_faved, fav.date_faved).
      value(_.title, fav.photo.title).
      value(_.owner, fav.photo.owner).
      value(_.owner_name, fav.photo.owner_name).
      value(_.date_upload, fav.photo.date_upload).
      value(_.date_taken, fav.photo.date_taken).
      value(_.count_views, fav.photo.count_views).
      value(_.count_faves, fav.photo.count_faves).
      value(_.count_comments, fav.photo.count_comments).
      value(_.tags, fav.photo.tags).
      value(_.machine_tags, fav.photo.machine_tags).
      value(_.urls, fav.photo.urls.toMap).
      future().
      map(_ => true)
  }

  def getByDashboardId(dashboard_id: UUID)  = {
    select.
      where(_.dashboard_id eqs dashboard_id).
      fetch()
  }



}
