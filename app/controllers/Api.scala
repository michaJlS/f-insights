package controllers

import controllers.actions._
import domain.service.{DashboardService, Stats}
import infrastructure.cassandra.FlickrAssistantDb
import models.flickr._
import javax.inject.Inject

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

  import JsonWrites._

  def userGetInfo(nsid:String) = userActionTpl(nsid).async(implicit request => {
      repository.
        getUserInfo(nsid, request.token).
        map {
          case Some(ui) =>  Ok(Json.toJson(ui))
          case _ => InternalServerError("Error while loading user info.")
        }
    })

  def getLastDashboard(nsid: String) = userActionTpl(nsid).async(implicit request => {
        dashboardService.
          getLastDashboard(nsid).
          map {
            case Some(dashboard) => Ok(Json.toJson[Dashboard](dashboard))
            case None => NotFound("Could not find dashboard")
          }
      })

  def userGetContacts(nsid: String) = myActionTpl(nsid).async(implicit request => {
        dashboardService.
          getContactsFromLastDashboard(nsid).
          map {
            case Some(contacts) => Ok(Json.toJson(contacts))
            case _ => InternalServerError("Error during fetching contacts.")
          }
      })

  def statsFavsTags(nsid:String) = myActionTpl(nsid).async(implicit request => {
        val threshold = request.getQueryString("threshold").map(_.toInt).getOrElse(3)

        dashboardService.
          getFavouritesFromLastDashboard(nsid).
          map {
            case Some(favs) => Some(stats.favsTagsStats(favs, threshold))
            case None => None
          } .
          map {
            case Some(tagsStats) => Ok(Json.toJson(tagsStats))
            case None => InternalServerError("Error during preparing stats of favs tags")
          }
      })


  def statsFavsOwners(nsid:String) = myActionTpl(nsid).async( implicit request => {

    val threshold = request.getQueryString("threshold").map(_.toInt).getOrElse(3)

    dashboardService.
      getFavouritesFromLastDashboard(nsid).
      map {
        case Some(favs) => Some(stats.favsOwnersStats(favs))
        case None => None
      } .
      map {
        case Some(tagsStats) => Ok(Json.toJson(tagsStats))
        case None => InternalServerError("Error during preparing stats of favs tags")
      }

  } )


  def statsUserTags(nsid:String) = Action.async( implicit request => {
    Future.successful {InternalServerError("Not yet implemented") }
  } )


  def preload(nsid:String) = myActionTpl(nsid).async( implicit request => {

      val data = for {
        fFavs <- repository.getAllUserPublicFavoritesParallely(nsid, request.token)
        fContacts <- repository.getAllUserPublicContacts(nsid, request.token)
      } yield (fFavs, fContacts)

      data.
        flatMap({
          case (Some(favs), Some(contacts)) => dashboardService.buildNewDashboard(nsid, favs, contacts)
          case (_, _) => Future.successful {None}
        }).
        map({
          case Some(dashboardId) => Ok("ok")
          case _ => InternalServerError("Something went wrong.")
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
