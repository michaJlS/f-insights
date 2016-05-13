package controllers

import java.util.UUID
import javax.inject.Inject


import domain.service.DashboardService
import models.flickr._
import org.joda.time.DateTime
import play.api.Play._
import play.api._
import play.api.libs.json._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.ws.WSClient
import play.api.mvc._

import scala.concurrent.{Future}



class Api @Inject() (apiClient: WSClient) extends Controller with Flickr with Db
{

  val context = defaultContext
  val repository = apiRepository(apiClient)
  val dashboardService = new DashboardService(db)
  val stats = new Stats


  def userGetInfo(nsid:String) = Action.async( implicit request => {

      val userInfoFunc = (token:UserToken, userNsid:String, ti:TokenInfo) => {
        repository.
          getUserInfo(nsid, token).
          map {
            case Some(ui) =>  Ok(JsonWriters.userInfo.writes(ui))
            case _ => InternalServerError("Error while loading user info.")
          }
      }

      checkNsid(nsid, () => ifTokenIsOk(userInfoFunc))
  } )

  def userGetContacts(nsid:String) = Action.async( implicit request => {

    val userContactsFunc = (token:UserToken, userNsid:String, ti:TokenInfo) => {
      dashboardService.
        getContactsFromLastDashboard(userNsid).
        map {
          case Some(contacts) => Ok(JsonWriters.userContacts.writes(contacts))
          case _ => InternalServerError("Error during fetching contacts.")
        }
    }

    checkMyNsid(nsid, userContactsFunc)
  } )

  def statsFavsTags(nsid:String) = Action.async( implicit request => {

    val threshold = request.getQueryString("threshold").map(_.toInt).getOrElse(3)

    val favsTagsFunc = (token:UserToken, userNsid:String, ti:TokenInfo) => {
      dashboardService.
        getFavouritesFromLastDashboard(userNsid).
        map {
          case Some(favs) => Some(stats.favsTagsStats(favs, threshold))
          case None => None
        } .
        map {
          case Some(tagsStats) => Ok(JsonWriters.favsTagsStats.writes(tagsStats))
          case None => InternalServerError("Error during preparing stats of favs tags")
        }
    }

    checkMyNsid(nsid, favsTagsFunc)
  } )


  def statsFavsOwners(nsid:String) = Action.async( implicit request => {

    val threshold = request.getQueryString("threshold").map(_.toInt).getOrElse(3)

    val favsTagsFunc = (token:UserToken, userNsid:String, ti:TokenInfo) => {
      dashboardService.
        getFavouritesFromLastDashboard(userNsid).
        map {
          case Some(favs) => Some(stats.favsOwnersStats(favs))
          case None => None
        } .
        map {
          case Some(tagsStats) => Ok(JsonWriters.favsOwnersStats.writes(tagsStats))
          case None => InternalServerError("Error during preparing stats of favs tags")
        }
    }

    checkMyNsid(nsid, favsTagsFunc)
  } )


  def statsUserTags(nsid:String) = Action.async( implicit request => {
    Future {InternalServerError("Not yet implemented") }
  } )



  def getLastDashboard(nsid: String) = Action.async( implicit request => {

      val getDashboardFunc = (token:UserToken, userNsid:String, ti:TokenInfo) => {
        dashboardService.getLastDashboard(nsid).map({
          case Some(dashboard) => Ok(JsonWriters.dashboard.writes(dashboard))
          case None => NotFound("Could not find dashboard")
        })
      }

      checkNsid(nsid, () => ifTokenIsOk(getDashboardFunc))

  } )

  def preload(nsid:String) = Action.async( implicit request => {

    val buildDashboardFunc = (token:UserToken, userNsid:String, ti:TokenInfo) => {

      val data = for {
        fFavs <- repository.getAllUserPublicFavoritesParallely(userNsid, token)
        fContacts <- repository.getAllUserPublicContacts(userNsid, token)
      } yield (fFavs, fContacts)

      data.
        flatMap({
          case (Some(favs), Some(contacts)) => dashboardService.buildNewDashboard(userNsid, favs, contacts)
          case (_, _) => Future {None}
        }).
        map({
          case Some(dashboardId) => Ok("ok")
          case _ => InternalServerError("Something went wrong.")
        })
    }

    checkMyNsid(nsid, buildDashboardFunc)
  })

  private def checkNsid(nsid:String, f:(()=>Future[Result]))(implicit request:Request[AnyContent]):Future[Result] = {
    if (nsid.length == 0)
      Future {BadRequest("Provided `nsid` is empty.")}
    else
      f()
  }

  private def checkMyNsid(nsid:String, f:((UserToken, String, TokenInfo)=>Future[Result]))(implicit request:Request[AnyContent]):Future[Result] = {

    val isMyNsidFunc = (token:UserToken, userNsid:String, ti:TokenInfo) => {
      if (userNsid!=nsid)
        Future { Forbidden("You can only try to acces own dashboards.") }
      else
        f(token, userNsid, ti)
    }

    checkNsid(nsid, () => ifTokenIsOk(isMyNsidFunc))

  }

  private def ifTokenIsOk(f:((UserToken, String, TokenInfo) => Future[Result]))(implicit request:Request[AnyContent]) = {
    val token = for {
      t <- request.headers.get("fa_token")
      s <- request.headers.get("fa_secret")
    } yield UserToken(t, s)

    val userNsid = request.headers.get("fa_nsid")

    if (token.isEmpty || userNsid.isEmpty) {
      Future {BadRequest("Token details has not been provided.")}
    } else {
      repository
        .checkToken(token.get)
        .flatMap({
          case None => Future {Unauthorized("Provided token is invalid.")}
          case Some(ti) =>
            if (userNsid.get == ti.nsid)
              f(token.get, userNsid.get, ti)
            else
              Future {Unauthorized("Token mismatch.")}
        })
    }
  }

}
