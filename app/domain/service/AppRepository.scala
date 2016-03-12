package domain.service



import java.util.UUID
import models.flickr.{AppUserDetail, Favourite, Dashboard}
import scala.concurrent.{ExecutionContext, Future}


/**
  * Created by michalsznurawa on 12/03/16.
  */
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
    * @param dashboard_id
    * @param favs
    * @return
    */
  def insertFavourties(dashboard_id: UUID, favs: Seq[Favourite])(implicit executor:ExecutionContext) = {
    Future.sequence(favs.map(insertFavourite(dashboard_id, _))).map(_ => true)
  }

  def getFavouritesByDashboardId(dashboard_id: UUID)(implicit executor:ExecutionContext): Future[List[Favourite]]

  def insertUserDetail(detail: AppUserDetail)(implicit executor:ExecutionContext):Future[Boolean]

  def getUserDetail(nsid: String, key: String)(implicit executor:ExecutionContext): Future[Option[AppUserDetail]]

}

