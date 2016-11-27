package controllers

import javax.inject.{Inject, Singleton}

import akka.actor.{ActorSystem, Props}
import controllers.actions._
import controllers.actors.messages.{PreloadDashboard, PreloadPhotosFavs}
import domain.entities.Dashboard
import domain.service.{DashboardService, Stats}
import infrastructure.cassandra.FlickrAssistantDb
import infrastructure.flickr.ApiRepository
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._
import play.api.libs.ws.WSClient
import play.api.mvc._

import scala.concurrent.Future
import scalaz.Scalaz._
import scalaz._

@Singleton
class Api @Inject() (system: ActorSystem, apiClient: WSClient, db: FlickrAssistantDb, repository: ApiRepository) extends Controller
{


  val stats = new Stats
  val dashboardService = new DashboardService(db, stats)

  val context = defaultContext
  val faManager = system.actorOf(Props(new actors.Manager(db, repository, stats)), "famanager")

  import infrastructure.json.JsonWrites._

  def userGetInfo(nsid:String) = userActionTpl(nsid).async(implicit request => {
    repository.
      getUserInfo(nsid, request.token).
      map {
        case Some(ui) =>  Ok(Json.toJson(ui))
        case _ => InternalServerError(Json.toJson("Error while loading user info."))
      }
  })

  def getLastDashboard(nsid: String) = userActionTpl(nsid).async(implicit request => {
    dashboardService.
      getLastDashboard(nsid).
      map {
        case Some(dashboard) => Ok(Json.toJson[Dashboard](dashboard))
        case None => NotFound(Json.toJson("Could not find dashboard"))
      }
  })

  def userGetContacts(nsid: String) = myActionTpl(nsid).async(implicit request => {
    dashboardService.
      getContactsFromLastDashboard(nsid).
      map {
        case Some(contacts) => Ok(Json.toJson(contacts))
        case _ => InternalServerError(Json.toJson("Error during fetching contacts."))
      }
  })

  def statsFavsTags(nsid:String) = myActionTpl(nsid).async(implicit request => {
    val threshold = request.getQueryString("threshold").map(_.toInt).getOrElse(3)
    val fromTimestamp = request.getQueryString("from_timestamp").filter(_.length>0)
    val toTimestamp = request.getQueryString("to_timestamp").filter(_.length>0)

    OptionT(dashboardService.getFavouritesFromLastDashboard(nsid))
      .map{dashboardService.filterDateRange(_, fromTimestamp, toTimestamp)}
      .map(favs => Ok(Json.toJson(Some(stats.favsTagsStats(favs, threshold)))))
      .getOrElse(InternalServerError(Json.toJson("Error during preparing stats of favs tags")))
  })

  def statsFavsOwners(nsid:String) = myActionTpl(nsid).async( implicit request => {
    val threshold = request.getQueryString("threshold").map(_.toInt).getOrElse(3)
    val nonContactsOnly = request.getQueryString("non_contacts") == Some("true")
    val fromTimestamp = request.getQueryString("from_timestamp").filter(_.length>0)
    val toTimestamp = request.getQueryString("to_timestamp").filter(_.length>0)

    OptionT {
      if (nonContactsOnly)
        dashboardService.getFavourtiesForNonCotactsFromLastDashboard(nsid)
      else
        dashboardService.getFavouritesFromLastDashboard(nsid)
    }
      .map(dashboardService.filterDateRange(_, fromTimestamp, toTimestamp))
      .map(favs => Ok(Json.toJson(stats.favsOwnersStats(favs, threshold))))
      .getOrElse(InternalServerError(Json.toJson("Error during preparing stats of favs tags.")))
  } )

  def timeline(nsid: String) = myActionTpl(nsid).async( implicit request => {
    OptionT(dashboardService.getLastDashboard(nsid))
      .flatMapF(dashboard => db.getTimeline(dashboard.id))
      .map(timeline => Ok(Json.toJson(timeline)))
      .getOrElse(InternalServerError(jsmsg("Error during loading monthly stats.")))
  } )

  def monthlyStats(nsid: String) = myActionTpl(nsid).async( implicit request => {
    OptionT(dashboardService.getMonthlyStatsFromLastDashboard(nsid))
      .map(stats => Ok(Json.toJson(stats)))
      .getOrElse(InternalServerError(Json.toJson("Error during preparing monthly stats.")))
  } )

  def statsUserTags(nsid: String) = myActionTpl(nsid).async( implicit request => {
    OptionT(dashboardService.getUserPhotosFromLastDashboard(nsid))
      .map(photos => Ok(Json.toJson(stats.popularTags(photos))))
      .getOrElse(InternalServerError(Json.toJson("Error during preparing stats of tags.")))
  } )


  def statsFavingUsersFromDb(nsid: String) = myActionTpl(nsid).async( implicit request => {
    OptionT(dashboardService.getLastDashboard(nsid))
      .flatMapF(dashboard => db.getFavingUsers(dashboard.id))
      .map(favusers => Ok(Json.toJson(favusers)))
      .getOrElse(InternalServerError(jsmsg("Error during preparing stats of faving users")))
  })

  def statsFavingUsers(nsid: String) = myActionTpl(nsid).async( implicit request => {
    val nonContactsOnly = request.getQueryString("non_contacts") == Some("true")

    OptionT {
      if (nonContactsOnly)
        dashboardService.getReceivedFavouritesForNonCotacts(nsid)
      else
        dashboardService.getReceivedFavourites(nsid)
    }
      .map(favs => Ok(Json.toJson(stats.favingUsers(favs))))
      .getOrElse(InternalServerError(Json.toJson("Error during preparing stats of faving users.")))
  } )

  def backgroundPreload(nsid: String) = myActionTpl(nsid).async( implicit request => {

    dashboardService
      .createEmptyDashboard(nsid)
      .map {
        case Some(dashboardId) => {
          faManager ! PreloadDashboard(request.token, dashboardId, nsid)
          Ok(jsmsg("ok"))
        }
        case _ => InternalServerError(jsmsg("Something went wrong."))
      }

  })

  private def jsmsg(message: String) = Json.toJson(message)

  def preload(nsid:String) = myActionTpl(nsid).async( implicit request => {
    val futureFavs = repository.getAllUserPublicFavorites(nsid, request.token)
    val futureContacts = repository.getAllUserPublicContacts(nsid, request.token)
    val futurePhotos = repository.getAllUserPhotos(nsid, request.token)
    val data = for {
      favs <- futureFavs
      contacts <- futureContacts
      photos <- futurePhotos
    } yield (favs, contacts, photos)

    data.
      flatMap({
        case (Some(favs), Some(contacts), Some(photos)) => dashboardService.buildNewDashboard(nsid, favs, contacts, photos)
        case _ => Future.successful {None}
      }).
      map({
        case Some(dashboardId) => {
          data.foreach {
            case (_, _, Some(photos)) => faManager ! PreloadPhotosFavs(request.token, dashboardId, nsid, photos.map(_.id))
            case _ => ()
          }
          Created(Json.toJson("ok"))
        }
        case _ => InternalServerError(Json.toJson("Something went wrong."))
      })
  })

  private def userActionTpl(nsid:String) =
    Action
      .andThen(new CheckNsid(nsid))
      .andThen(new AttachUserToken)
      .andThen(new AttachTokenInfo(repository))
      .andThen(new CheckTokenInfo)

  private def myActionTpl(nsid:String) =
    Action
      .andThen(new CheckNsid(nsid))
      .andThen(new AttachUserToken)
      .andThen(new CheckIfMineNsid(nsid))
      .andThen(new AttachTokenInfo(repository))
      .andThen(new CheckTokenInfo)

}
