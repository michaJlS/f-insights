package domain.service

import java.util.UUID

import models.flickr.{Contact, AppUserDetail, Dashboard, Favourite}
import org.joda.time.DateTime

import scala.concurrent.{ExecutionContext, Future}


object DashboardService {

  val dashboard_property_name = "last_dashboard"

}

class DashboardService(appRepository: AppRepository)
{

  def getLastDashboard(nsid: String)(implicit executor:ExecutionContext) = {
    appRepository.
      getUserDetail(nsid, DashboardService.dashboard_property_name).
      flatMap {
        case Some(detail) => appRepository.getDashboard(nsid, detail.detail_value)
        case None => Future.successful {None}
      }
  }

  def buildNewDashboard(nsid: String, favs: Seq[Favourite], contacts: Seq[Contact])(implicit executor:ExecutionContext) = {

    val dashboard = new Dashboard(nsid, UUID.randomUUID(), DateTime.now())
    val dashboardInfo = AppUserDetail(nsid, DashboardService.dashboard_property_name, dashboard.id.toString)

    appRepository.
      insertDashboard(dashboard).
      flatMap(_ => appRepository.insertFavourties(dashboard.id, favs)).
      flatMap(_ => appRepository.insertUserDetail(dashboardInfo)).
      flatMap(_ => appRepository.insertContacts(dashboard.id, contacts)).
      map(_ => Some(dashboard.id))
  }

  def getFavouritesFromLastDashboard(nsid:String)(implicit executor:ExecutionContext): Future[Option[List[Favourite]]] = {
    getLastDashboard(nsid).
      flatMap({
        case Some(dashboard) => appRepository.getFavouritesByDashboardId(dashboard.id).map((favs:List[Favourite]) => Some(favs))
        case _ => Future.successful {None}
      })
  }

  def getContactsFromLastDashboard(nsid:String)(implicit executor:ExecutionContext): Future[Option[List[Contact]]] = {
    getLastDashboard(nsid).
      flatMap({
        case Some(dashboard) => appRepository.getContactsByDashboardId(dashboard.id).map((contacts:List[Contact]) => Some(contacts))
        case _ => Future.successful {None}
      })
  }

}
