package infrastructure.cassandra.table

import com.websudos.phantom.dsl._
import java.util.UUID

import domain.entities.{PhotoExcerpt, PhotoUrls}


class Photos extends CassandraTable[Photos, PhotoExcerpt] {

  override lazy val tableName = "photos"

  object dashboard_id extends UUIDColumn(this) with PrimaryKey[UUID]
  object photo_id extends StringColumn(this) with ClusteringOrder[String]
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
  object urls extends MapColumn[Photos, PhotoExcerpt, String, String](this) //   urls frozen <photo_urls>,

  override def fromRow(r: Row) = PhotoExcerpt(photo_id(r), title(r), owner(r), owner_name(r), date_upload(r), date_taken(r),
    count_views(r), count_faves(r), count_comments(r), tags(r), machine_tags(r), PhotoUrls(urls(r))
  )

}


abstract class ConcretePhotos extends Photos with RootConnector {

  def insertPhoto(dashboard_id: UUID, photo: PhotoExcerpt) = {
    insert.
      value(_.dashboard_id, dashboard_id).
      value(_.photo_id, photo.id).
      value(_.title, photo.title).
      value(_.owner, photo.owner).
      value(_.owner_name, photo.owner_name).
      value(_.date_upload, photo.date_upload).
      value(_.date_taken, photo.date_taken).
      value(_.count_views, photo.count_views).
      value(_.count_faves, photo.count_faves).
      value(_.count_comments, photo.count_comments).
      value(_.tags, photo.tags).
      value(_.machine_tags, photo.machine_tags).
      value(_.urls, photo.urls.toMap).
      future().
      map(_ => true)
  }

  def getByDashboardId(dashboard_id: UUID) =
    select.
      where(_.dashboard_id eqs dashboard_id).
      fetch()

}
