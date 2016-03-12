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
            case Some(ui) => {
              Ok(JsonWriters.userInfo.writes(ui))
            }
            case _ => InternalServerError("Error while loading user info.")
          }
      }

      checkNsid(nsid, () => ifTokenIsOk(userInfoFunc))

  } )

  def statsFavsTags(nsid:String) = Action.async( implicit request => {

      val favsTagsFunc = (token:UserToken, userNsid:String, ti:TokenInfo) => {
        repository.
          getAllUserPublicFavoritesParallely(nsid, token).
          map {
            case Some(favs) => Right(stats.tagsStats(favs))
            case _ => Left(InternalServerError("Error while loading favourties list."))
          }.
          map {
            case Right(tags) => {
              val json = JsArray(tags.map({
                case (tag:String, count:Int) => Json.obj("tag" -> tag, "count" -> count.toString)
              }).toSeq)
              Ok(Json.toJson(json))
            }
            case Left(resp) => resp
          }
      };

      checkNsid(nsid, () => ifTokenIsOk(favsTagsFunc))
  } )

  def statsFavsOwners(nsid:String) = Action.async( implicit request => {

      val favsOwnersFunc = (token:UserToken, userNsid:String, ti:TokenInfo) => {
        repository
          .getAllUserPublicFavoritesParallely(nsid, token)
          .map({
            case Some(favs) => Right(stats.ownersStats(favs))
            case _ => Left(InternalServerError("Error while loading favourties list."))
          })
          .map({
            case Right(tags) => {
              val json = JsArray(tags.map({
                case (owner:String, count:Int) => Json.obj("owner" -> owner, "count" -> count.toString)
              }).toSeq)
              Ok(Json.toJson(json))
            }
            case Left(resp) => resp
          })
      }

      checkNsid(nsid, () => ifTokenIsOk(favsOwnersFunc))

  } )

  def statsUserTags(nsid:String) = Action.async( implicit request => {
    Future {InternalServerError("Not yet implemented") }
  } )



  def getLastDashboard(nsid: String) = Action.async( implicit request => {

      val getDashboardFunc = (token:UserToken, userNsid:String, ti:TokenInfo) => {
        if (userNsid!=nsid)
          Future { Forbidden("You can only try to acces own dashboards.") }
        else
          dashboardService.getLastDashboard(nsid).map({
            case Some(dashboard) => Ok(JsonWriters.dashboard.writes(dashboard))
            case None => NotFound("Could not find dashboard")
          })
      }

      checkNsid(nsid, () => ifTokenIsOk(getDashboardFunc))

  } )

  def preload(nsid:String) = Action.async( implicit request => {

    val buildDashboardFunc = (token:UserToken, userNsid:String, ti:TokenInfo) => {
      if (userNsid!=nsid)
        Future { Forbidden("You can only try to acces own dashboards.") }
      else
        repository.
          getAllUserPublicFavoritesParallely(userNsid, token).
          flatMap {
            case Some(favs) => dashboardService.buildNewDashboard(userNsid, favs)
            case None => Future {None}
          }.
          map {
            case Some(dashboardId) => Ok("ok")
            case None => InternalServerError("Something went wrong.")
          }
    }

    checkNsid(nsid, () => ifTokenIsOk(buildDashboardFunc))
  })

  private def checkNsid(nsid:String, f:(()=>Future[Result]))(implicit request:Request[AnyContent]):Future[Result] = {
    if (nsid.length == 0)
      Future {BadRequest("Provided `nsid` is empty.")}
    else
      f()
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
