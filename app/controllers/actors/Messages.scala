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

//case class PreloadRelativesPhotosAndContacts(token: UserToken, dashboardId: UUID, relatives: Seq[String])

case class DoStats(token: UserToken, dashboardId: UUID)

case class PreloadRelativePhotos(token: UserToken, dashboardId: UUID, nsid: String) extends PreloadTask
case class PreloadRelativeContacts(token: UserToken, dashboardId: UUID, nsid: String) extends PreloadTask

case class LoadedPhotoFavs(dashboardId: UUID, photo: String, photoFavs: Seq[PhotoFavourite])
case class LoadedPhotos(dashboardId: UUID, photos: Seq[PhotoExcerpt])
case class LoadedContacts(dashboardId: UUID, contacts: Seq[Contact])
case class LoadedFavs(dashboardId: UUID, favs: Seq[Favourite])
case class LoadedTimeline(dashboardId: UUID, timeline: Seq[MonthlyStats])
case class LoadedFavingUsers(dashboardId: UUID, favingUserStats: Seq[FavingUserStats])

case class LoadedRelativePhotos(dashboardId: UUID, nsid: String, total: Int, photos: Seq[PhotoExcerpt])
case class LoadedRelativeContacts(dashboardId: UUID, nsid: String, total: Int, contacts: Seq[Contact])
case class LoadedRelative(dashboardId: UUID, relative: Relative)
