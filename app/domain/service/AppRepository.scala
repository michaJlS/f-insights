package domain.service


import java.util.UUID
import models.flickr.{Contact, AppUserDetail, Favourite, Dashboard}
import scala.concurrent.{ExecutionContext, Future}


trait AppRepository
{

  /**
    * @throws Exception
    * @param dashboard
    * @return
    */
  def insertDashboard(dashboard: Dashboard)(implicit executor:ExecutionContext): Future[Boolean]

  def getDashboard(nsid: String, dashboard_id: UUID)(implicit executor:ExecutionContext):  Future[Option[Dashboard]]

  def getDashboard(nsid: String, dashboard_id: String)(implicit executor:ExecutionContext): Future[Option[Dashboard]] = getDashboard(nsid, UUID.fromString(dashboard_id))

  /**
    * @throws Exception
    * @param dashboard_id
    * @param fav
    * @return
    */
  def insertFavourite(dashboard_id: UUID, fav: Favourite)(implicit executor:ExecutionContext): Future[Boolean]

  /**
    * @throws Exception
    * @param dashboardId
    * @param favs
    * @return
    */
  def insertFavourties(dashboardId: UUID, favs: Seq[Favourite])(implicit executor:ExecutionContext) = Future.sequence(favs.map(insertFavourite(dashboardId, _))).map(_ => true)

  def getFavouritesByDashboardId(dashboard_id: UUID)(implicit executor:ExecutionContext): Future[List[Favourite]]

  def insertUserDetail(detail: AppUserDetail)(implicit executor:ExecutionContext):Future[Boolean]

  def getUserDetail(nsid: String, key: String)(implicit executor:ExecutionContext): Future[Option[AppUserDetail]]

  def insertContact(dashboardId:UUID, contact:Contact)(implicit executor:ExecutionContext): Future[Boolean]

  def insertContacts(dashboardId:UUID, contacts:Seq[Contact])(implicit executor:ExecutionContext) = Future.sequence(contacts.map(insertContact(dashboardId, _))).map(_ => true)

  def getContactsByDashboardId(dashboardId: UUID)(implicit executor:ExecutionContext): Future[List[Contact]]

}
