package domain.service

import java.util.UUID

import domain.entities.{AppUserDetail, Contact, Dashboard, Favourite}
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

  def getFavourtiesForNonCotactsFromLastDashboard(nsid:String)(implicit executor:ExecutionContext): Future[Option[List[Favourite]]] = {
    val favsFuture = getFavouritesFromLastDashboard(nsid)
    val contactsFuture = getContactsFromLastDashboard(nsid).map {
      case Some(contacts) => Some(contacts.map(_.nsid).toSet)
      case _ => None
    }

    favsFuture.flatMap(favs => contactsFuture.map( contacts =>
      (favs, contacts) match {
        case (Some(fs), Some(cs)) => Some(fs.filterNot(fr => cs.contains(fr.photo.owner)))
        case _ => None
      }
    ))
  }

  def filterDateRange(favs: List[Favourite], from: Option[String], to: Option[String]) = (from, to) match {
      case (Some(f), Some(t)) => favs.filter(fav => fav.date_faved.compare(t) < 1 && fav.date_faved.compare(f) > -1)
      case (Some(f), None)    => favs.filter(_.date_faved.compare(f) > -1)
      case (None, Some(t))    => favs.filter(_.date_faved.compare(t) < 1)
      case (None, None)       => favs
    }

}
