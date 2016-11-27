package domain.service


import java.util.UUID

import domain.entities._

import scala.concurrent.{ExecutionContext, Future}


trait AppRepository
{

  /**  @throws Exception */
  def insertDashboard(dashboard: Dashboard)(implicit executor:ExecutionContext): Future[Boolean]

  def getDashboard(nsid: String, dashboard_id: UUID)(implicit executor:ExecutionContext):  Future[Option[Dashboard]]

  def getDashboard(nsid: String, dashboard_id: String)(implicit executor:ExecutionContext): Future[Option[Dashboard]] = getDashboard(nsid, UUID.fromString(dashboard_id))

  /** @throws Exception */
  def insertFavourite(dashboard_id: UUID, fav: Favourite)(implicit executor:ExecutionContext): Future[Boolean]

  /** @throws Exception */
  def insertFavourties(dashboardId: UUID, favs: Seq[Favourite])(implicit executor:ExecutionContext) = Future.sequence(favs.map(insertFavourite(dashboardId, _))).map(_ => true)

  def getFavouritesByDashboardId(dashboard_id: UUID)(implicit executor:ExecutionContext): Future[List[Favourite]]

  def insertUserDetail(detail: AppUserDetail)(implicit executor:ExecutionContext):Future[Boolean]

  def getUserDetail(nsid: String, key: String)(implicit executor:ExecutionContext): Future[Option[AppUserDetail]]

  def insertContact(dashboardId:UUID, contact:Contact)(implicit executor:ExecutionContext): Future[Boolean]

  def insertContacts(dashboardId:UUID, contacts:Seq[Contact])(implicit executor:ExecutionContext) = Future.sequence(contacts.map(insertContact(dashboardId, _))).map(_ => true)

  def getContactsByDashboardId(dashboardId: UUID)(implicit executor:ExecutionContext): Future[List[Contact]]

  def insertUserPhoto(dashboardId:UUID, photo: PhotoExcerpt)(implicit executor:ExecutionContext): Future[Boolean]

  def insertUserPhotos(dashboardId:UUID, photos: Seq[PhotoExcerpt])(implicit executor:ExecutionContext) = Future.sequence(photos.map(insertUserPhoto(dashboardId, _))).map(_ => true)

  def getPhotosByDashboardId(dashboardId: UUID)(implicit executor:ExecutionContext): Future[List[PhotoExcerpt]]

  def insertPhotoFavourite(dashboardId:UUID, photoFav: PhotoFavourite)(implicit executor:ExecutionContext): Future[Boolean]

  def getPhotoFavouritesByDashboardId(dashboardId: UUID)(implicit executor:ExecutionContext): Future[List[PhotoFavourite]]

  def insertPhotoFavourites(dashboardId:UUID, photoFavs: Seq[PhotoFavourite])(implicit executor:ExecutionContext): Future[Boolean] =
    Future.sequence(photoFavs.map(insertPhotoFavourite(dashboardId, _))).map(_ => true)

  def insertFavingUser(dashboardId: UUID, user: FavingUserStats)(implicit executor:ExecutionContext): Future[Boolean]

  def insertFavingUsers(dashboardId: UUID, users: Seq[FavingUserStats])(implicit executor:ExecutionContext): Future[Boolean] =
    Future.sequence(users.map(insertFavingUser(dashboardId, _))).map(_ => true)

  def getFavingUsers(dashboardId: UUID)(implicit executor:ExecutionContext): Future[List[FavingUserStats]]

  def insertRelative(dashboardId: UUID, relative: Relative)(implicit executor:ExecutionContext): Future[Boolean]

  def insertRelatives(dashboardId: UUID, relatives: Seq[Relative])(implicit executor:ExecutionContext): Future[Boolean] =
    Future.sequence(relatives.map(insertRelative(dashboardId, _))).map(_ => true)

  def getRelatives(dashboardId: UUID)(implicit executor:ExecutionContext): Future[List[Relative]]

  def insertMonthlyStats(dashboardId: UUID, monthlyStats: MonthlyStats)(implicit executor:ExecutionContext): Future[Boolean]

  def insertTimeline(dashboardId: UUID, timeline: Seq[MonthlyStats])(implicit executor:ExecutionContext): Future[Boolean] =
    Future.sequence(timeline.map(insertMonthlyStats(dashboardId, _))).map(_ => true)

  def getTimeline(dashboardId: UUID)(implicit executor:ExecutionContext): Future[List[MonthlyStats]]

}
