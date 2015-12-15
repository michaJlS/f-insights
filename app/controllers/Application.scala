package controllers

import java.util.Calendar
import javax.inject.Inject

import models.flickr.{ApiClient => FlickrApiClient, PhotoExcerpt, ApiRepository, UserInfo, ResponseParser}
import play.api.Play._
import play.api._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.ws.WSClient
import play.api.mvc._


import scala.concurrent.Future


class Application @Inject() (apiClient: WSClient) extends Controller with Base with Flickr
{

  val context = defaultContext

  def index = Action { implicit request =>
   if (isLogged) {
     Ok(views.html.index("Flickr Assistant."))
   } else {
     TemporaryRedirect(routes.Auth.login.absoluteURL)
   }
  }


  private def stringifyPhotos(photos:Seq[PhotoExcerpt]):String = {
    photos.length + " \n\n" + photos.map(" " + _.toString + "\n\n").fold("")(_ + _)
  }

  def test = Action.async( implicit request => {
      val fApi = getFlickrApiClient
      val parser = new ResponseParser
      val repo = new ApiRepository(fApi, parser)


      repo.checkToken.flatMap(_ match {
        case Some(token) => repo.getAllUserPublicFavoritesParallely(token.nsid)
        case None => Future {None}
      }).map { res =>
        println(Calendar.getInstance().getTime)
        res match {
          case Some(photos) => {
            println(Calendar.getInstance().getTime)
            photos
            Ok(stringifyPhotos(photos))
          }
          case _ => InternalServerError("Nothing")
        }
      }
  } )


  private def getFlickrApiClient(implicit request:RequestHeader) = {
    new FlickrApiClient(current.configuration.getString("alerf.flickr.rest.url").get, apiClient, consumerKey, getRequestToken.get)
  }

}
