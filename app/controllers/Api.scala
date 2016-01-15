package controllers

import javax.inject.Inject


import models.flickr._
import play.api.Play._
import play.api._
import play.api.libs.json._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.ws.WSClient
import play.api.mvc._

import scala.concurrent.{Future}


class Api @Inject() (apiClient: WSClient) extends Controller with Flickr
{

  val context = defaultContext
  val repository = apiRepository(apiClient)
  val stats = new Stats


  private def verifyToken(f:((UserToken, String, TokenInfo) => Future[Result]))(implicit request:Request[AnyContent]) = {
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
          case Some(ti) => if (userNsid.get == ti.nsid) f(token.get, userNsid.get, ti) else Future {Unauthorized("Token mismatch.")}
          case None => Future {Unauthorized("Provided token is invalid.")}
        })
    }
  }

  def statsFavsTags(nsid:String) = Action.async( implicit request => {
    if (nsid.length > 0) {
      verifyToken((token:UserToken, userNsid:String, ti:TokenInfo) => {
        repository
          .getAllUserPublicFavoritesParallely(nsid, token)
          .map({
            case Some(favs) => Right(stats.tagsStats(favs))
            case _ => Left(InternalServerError("Error while loading favourties list."))
          })
          .map({
            case Right(tags) => {
              val json = JsArray(tags.map({
                case (tag:String, count:Int) => Json.obj("tag" -> tag, "count" -> count.toString)
              }).toSeq)
              Ok(Json.toJson(json))
            }
            case Left(resp) => resp
          })
      })
    } else {
      Future {BadRequest("Provided `nsid` is empty.")}
    }
  } )

  def statsFavsOwners(nsid:String) = Action.async( implicit request => {
    if (nsid.length > 0) {
      verifyToken((token:UserToken, userNsid:String, ti:TokenInfo) => {
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
      })
    } else {
      Future {BadRequest("Provided `nsid` is empty.")}
    }
  } )

  def statsUserTags(nsid:String) = Action.async( implicit request => {
    Future {InternalServerError("Not yet implemented") }
  } )

}
