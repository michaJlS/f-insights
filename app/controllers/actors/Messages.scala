package controllers.actors.messages

import domain.entities._
import java.util.UUID

case class PreloadDashboard(token: UserToken, dashboardId: UUID, nsid: String)
case class PreloadPhotosFavs(token: UserToken, dashboardId: UUID, owner: String, photos: Seq[String])

sealed trait PreloadTask

case class PreloadPhotoFavs(token: UserToken, dashboardId: UUID, owner: String, photo: String) extends PreloadTask
case class PreloadPhotos(token: UserToken, dashboardId: UUID, nsid: String) extends PreloadTask
case class PreloadContacts(token: UserToken, dashboardId: UUID, nsid: String) extends PreloadTask
case class PreloadFavs(token: UserToken, dashboardId: UUID, nsid: String) extends PreloadTask

case class DoStats(token: UserToken, dashboardId: UUID)

trait BasicDashbaordInfo
trait RelativesInfo {
  def dashboardId: UUID
  def nsid: String
}

case class DoDashboardStats(token: UserToken, dashboardId: UUID, favs: Seq[Favourite], contacts: Seq[Contact],
                            photos: Seq[PhotoExcerpt], photoFavourites: Map[String, Seq[PhotoFavourite]])

case class DoRelativeStats(token: UserToken, dashboardId: UUID, nsid: String, realname: String, username: String,
                           followed: Boolean, faved: Seq[Favourite], faving: Seq[PhotoFavourite],
                           contacts: Seq[Contact], totalContacts: Int,
                           photos: Seq[PhotoExcerpt], totalPhotos: Int)

case class PreloadRelativePhotos(token: UserToken, dashboardId: UUID, nsid: String) extends PreloadTask
case class PreloadRelativeContacts(token: UserToken, dashboardId: UUID, nsid: String) extends PreloadTask

case class GatherData(token: UserToken, dashboardId: UUID) extends BasicDashbaordInfo

case class LoadedPhotoFavs(dashboardId: UUID, photo: String, photoFavs: Seq[PhotoFavourite]) extends BasicDashbaordInfo
case class LoadedPhotos(dashboardId: UUID, photos: Seq[PhotoExcerpt]) extends BasicDashbaordInfo
case class LoadedContacts(dashboardId: UUID, contacts: Seq[Contact]) extends BasicDashbaordInfo
case class LoadedFavs(dashboardId: UUID, favs: Seq[Favourite]) extends BasicDashbaordInfo
case class LoadedTimeline(dashboardId: UUID, timeline: Seq[MonthlyStats]) extends BasicDashbaordInfo
case class LoadedFavingUsers(dashboardId: UUID, favingUserStats: Seq[FavingUserStats]) extends BasicDashbaordInfo

case class LoadedRelativePhotos(dashboardId: UUID, nsid: String, total: Int, photos: Seq[PhotoExcerpt]) extends RelativesInfo
case class LoadedRelativeContacts(dashboardId: UUID, nsid: String, total: Int, contacts: Seq[Contact]) extends RelativesInfo
case class LoadedRelative(dashboardId: UUID, relative: Relative) extends RelativesInfo
{
  def nsid = relative.nsid
}
