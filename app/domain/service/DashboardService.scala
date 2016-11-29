package domain.service

import java.util.UUID

import domain.entities._
import org.joda.time.DateTime

import scala.concurrent.{ExecutionContext, Future}
import scalaz._
import Scalaz._

class DashboardService(appRepository: AppRepository, stats: Stats) {

  def getLastDashboard(nsid: String)(implicit executor: ExecutionContext): Future[Option[Dashboard]] =
    OptionT(appRepository.getUserDetail(nsid, DashboardService.dashboard_property_name))
      .flatMap(detail => OptionT(appRepository.getDashboard(nsid, detail.detail_value)))
      .run

  def createEmptyDashboard(nsid: String)(implicit executor:ExecutionContext) = {
    val dashboard = Dashboard(nsid, UUID.randomUUID(), DateTime.now())
    val dashboardInfo = AppUserDetail(nsid, DashboardService.dashboard_property_name, dashboard.id.toString)

    appRepository
      .insertDashboard(dashboard)
      .flatMap {ok =>
        if (ok)
          appRepository.insertUserDetail(dashboardInfo)
        else
          Future.successful(false)
      }
      .map(if (_) Some(dashboard.id) else None)
  }

  def buildNewDashboard(
                         nsid: String,
                         favs: Seq[Favourite],
                         contacts: Seq[Contact],
                         photos: Seq[PhotoExcerpt]
                       )
                       (implicit executor:ExecutionContext) = {
    val dashboard = Dashboard(nsid, UUID.randomUUID(), DateTime.now())
    val dashboardInfo = AppUserDetail(nsid, DashboardService.dashboard_property_name, dashboard.id.toString)

    appRepository.
      insertDashboard(dashboard).
      flatMap(_ => {
        val favsF = appRepository.insertFavourties(dashboard.id, favs)
        val contactsF = appRepository.insertContacts(dashboard.id, contacts)
        val photosF = appRepository.insertUserPhotos(dashboard.id, photos)
        val userF = appRepository.insertUserDetail(dashboardInfo)
        for {
          _ <- favsF
          _ <- contactsF
          _ <- photosF
          _ <- userF
        } yield true
      }).
      map(_ => Some(dashboard.id))
  }

  def getFavourites(nsid: String)(implicit executor:ExecutionContext): Future[Option[List[Favourite]]] =
    OptionT(getLastDashboard(nsid))
      .flatMapF(dashboard => appRepository.getFavouritesByDashboardId(dashboard.id, nsid))
      .run


  def getUserPhotosFromLastDashboard(nsid: String)(implicit executor:ExecutionContext): Future[Option[List[PhotoExcerpt]]] =
    OptionT(getLastDashboard(nsid))
      .flatMapF(dashboard => appRepository.getPhotosByDashboardId(dashboard.id, nsid))
      .run

  def getContacts(nsid: String)(implicit executor:ExecutionContext): Future[Option[List[Contact]]] =
    OptionT(getLastDashboard(nsid))
      .flatMapF(dashboard => appRepository.getContactsByDashboardId(dashboard.id, nsid))
      .run

  def getReceivedFavourites(nsid: String)(implicit ec: ExecutionContext): Future[Option[List[PhotoFavourite]]] = {
    OptionT(getLastDashboard(nsid))
      .flatMapF(dashboard => appRepository.getPhotoFavouritesByDashboardId(dashboard.id))
      .run
  }

  def getReceivedFavouritesForNonCotacts(nsid: String)(implicit ec: ExecutionContext): Future[Option[List[PhotoFavourite]]] = {
    val favsFuture = OptionT(getReceivedFavourites(nsid))
    val contactsFuture = OptionT(getContacts(nsid))

    (for {
      favs <- favsFuture
      contacts <- contactsFuture
      contactsIds = contacts.map(_.nsid).toSet
    } yield favs.filterNot(fr => contactsIds.contains(fr.faved_by)))
      .run
  }

  def getFavourtiesForNonCotacts(nsid:String)(implicit executor:ExecutionContext): Future[Option[List[Favourite]]] = {
    val favsFuture = OptionT(getFavourites(nsid))
    val contactsFuture = OptionT(getContacts(nsid))

    (for {
      favs <- favsFuture
      contacts <- contactsFuture
      contactsIds = contacts.map(_.nsid).toSet
    } yield favs.filterNot(fr => contactsIds.contains(fr.photo.owner)))
      .run
  }

  def getMonthlyStats(nsid: String)(implicit executor:ExecutionContext): Future[Option[Seq[MonthlyStats]]] = {
    val favsFuture = OptionT(getFavourites(nsid))
    val photosFuture = OptionT(getUserPhotosFromLastDashboard(nsid))
    val gotFavsFuture = OptionT(getReceivedFavourites(nsid))

    (for {
      favs <- favsFuture
      photos <- photosFuture
      gotFavs <- gotFavsFuture
    } yield stats.monthlyStats(photos, favs, gotFavs))
      .run
  }

  def getRelatives(nsid: String)(implicit executor:ExecutionContext): Future[Option[List[Relative]]] =
    OptionT(getLastDashboard(nsid))
      .flatMapF(d => appRepository.getRelatives(d.id))
      .run

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
