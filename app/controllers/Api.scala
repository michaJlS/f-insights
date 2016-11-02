package controllers

import controllers.actions._
import domain.service.{DashboardService, Stats}
import infrastructure.cassandra.FlickrAssistantDb
import javax.inject.Inject

import domain.entities.Dashboard
import infrastructure.flickr.ApiRepository
import play.api.libs.json._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.ws.WSClient
import play.api.mvc._

import scala.concurrent.Future

class Api @Inject() (apiClient: WSClient, db:FlickrAssistantDb, repository: ApiRepository) extends Controller
{

  val context = defaultContext
  val dashboardService = new DashboardService(db)
  val stats = new Stats

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
    val fromTimestamp = request.getQueryString("form_timestamp").filter(_.length>0)
    val toTimestamp = request.getQueryString("to_timestamp").filter(_.length>0)

    dashboardService.
      getFavouritesFromLastDashboard(nsid).
      map {_.map(dashboardService.filterDateRange(_, fromTimestamp, toTimestamp))} .
      map {
        case Some(favs) => {
          val tagsStats = Some(stats.favsTagsStats(favs, threshold))
          Ok(Json.toJson(tagsStats))
        }
        case _ => InternalServerError(Json.toJson("Error during preparing stats of favs tags"))
      }
  })

  def statsFavsOwners(nsid:String) = myActionTpl(nsid).async( implicit request => {
    val threshold = request.getQueryString("threshold").map(_.toInt).getOrElse(3)
    val nonContactsOnly = request.getQueryString("non_contacts") == Some("true")
    val fromTimestamp = request.getQueryString("form_timestamp").filter(_.length>0)
    val toTimestamp = request.getQueryString("to_timestamp").filter(_.length>0)


    val favsFuture = if (nonContactsOnly)
      dashboardService.getFavourtiesForNonCotactsFromLastDashboard(nsid)
    else
      dashboardService.getFavouritesFromLastDashboard(nsid)

    favsFuture.
      map {_.map(dashboardService.filterDateRange(_, fromTimestamp, toTimestamp))} .
      map {
        case Some(favs) => {
          val tagsStats = stats.favsOwnersStats(favs, threshold)
          Ok(Json.toJson(tagsStats))
        }
        case _ => InternalServerError(Json.toJson("Error during preparing stats of favs tags"))
      }
  } )

  def statsUserTags(nsid:String) = Action.async( implicit request => {
    Future.successful {InternalServerError(Json.toJson("Not yet implemented")) }
  } )


  def preload(nsid:String) = myActionTpl(nsid).async( implicit request => {
    val data = for {
      fFavs <- repository.getAllUserPublicFavoritesParallely(nsid, request.token)
      fContacts <- repository.getAllUserPublicContacts(nsid, request.token)
    } yield (fFavs, fContacts)

    data.
      flatMap({
        case (Some(favs), Some(contacts)) => dashboardService.buildNewDashboard(nsid, favs, contacts)
        case _ => Future.successful {None}
      }).
      map({
        case Some(dashboardId) => Created(Json.toJson("ok"))
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
