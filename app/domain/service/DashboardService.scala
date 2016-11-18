package domain.service

import java.util.UUID

import domain.entities.{AppUserDetail, Contact, Dashboard, Favourite}
import org.joda.time.DateTime

import scala.concurrent.{ExecutionContext, Future}
import scalaz._
import Scalaz._

class DashboardService(appRepository: AppRepository) {

  def getLastDashboard(nsid: String)(implicit executor: ExecutionContext): Future[Option[Dashboard]] =
    OptionT(appRepository.getUserDetail(nsid, DashboardService.dashboard_property_name))
      .flatMap(detail => OptionT(appRepository.getDashboard(nsid, detail.detail_value)))
      .run

  def buildNewDashboard(nsid: String, favs: Seq[Favourite], contacts: Seq[Contact])(implicit executor:ExecutionContext) = {
    val dashboard = new Dashboard(nsid, UUID.randomUUID(), DateTime.now())
    val dashboardInfo = AppUserDetail(nsid, DashboardService.dashboard_property_name, dashboard.id.toString)

    appRepository.
      insertDashboard(dashboard).
      flatMap(_ => {
        val favsF = appRepository.insertFavourties(dashboard.id, favs)
        val contactsF = appRepository.insertContacts(dashboard.id, contacts)
        val userF = appRepository.insertUserDetail(dashboardInfo)
        for {
          _ <- favsF
          _ <- contactsF
          _ <- userF
        } yield true
      }).
      map(_ => Some(dashboard.id))
  }

  def getFavouritesFromLastDashboard(nsid:String)(implicit executor:ExecutionContext): Future[Option[List[Favourite]]] =
    OptionT(getLastDashboard(nsid))
      .flatMapF(dashboard => appRepository.getFavouritesByDashboardId(dashboard.id))
      .run

  def getContactsFromLastDashboard(nsid:String)(implicit executor:ExecutionContext): Future[Option[List[Contact]]] =
    OptionT(getLastDashboard(nsid))
      .flatMapF(dashboard => appRepository.getContactsByDashboardId(dashboard.id))
      .run

  def getFavourtiesForNonCotactsFromLastDashboard(nsid:String)(implicit executor:ExecutionContext): Future[Option[List[Favourite]]] = {
    val favsFuture = OptionT(getFavouritesFromLastDashboard(nsid))
    val contactsFuture = OptionT(getContactsFromLastDashboard(nsid))

    (for {
      favs <- favsFuture
      contacts <- contactsFuture
      contactsIds = contacts.map(_.nsid).toSet
    } yield favs.filterNot(fr => contactsIds.contains(fr.photo.owner)))
      .run
  }

  def filterDateRange(favs: List[Favourite], from: Option[String], to: Option[String]) = (from, to) match {
      case (Some(f), Some(t)) => favs.filter(fav => fav.date_faved.compare(t) < 1 && fav.date_faved.compare(f) > -1)
      case (Some(f), None)    => favs.filter(_.date_faved.compare(f) > -1)
      case (None, Some(t))    => favs.filter(_.date_faved.compare(t) < 1)
      case (None, None)       => favs
    }

}

object DashboardService {
  val dashboard_property_name = "last_dashboard"
}
