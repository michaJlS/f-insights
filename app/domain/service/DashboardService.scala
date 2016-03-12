package domain.service

import java.util.UUID

import models.flickr.{AppUserDetail, Dashboard, Favourite}
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
        case None => Future {None}
      }
  }

  def buildNewDashboard(nsid: String, favs: Seq[Favourite])(implicit executor:ExecutionContext) = {

    val dashboard = new Dashboard(nsid, UUID.randomUUID(), DateTime.now())
    val dashboardInfo = AppUserDetail(nsid, DashboardService.dashboard_property_name, dashboard.id.toString)

    appRepository.
      insertDashboard(dashboard).
      flatMap(_ => appRepository.insertFavourties(dashboard.id, favs)).
      flatMap(_ => appRepository.insertUserDetail(dashboardInfo))
      .map(_ => Some(dashboard.id))
  }


}
