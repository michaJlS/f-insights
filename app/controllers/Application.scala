package controllers

import javax.inject.Inject

import models.alerf.flickr.{ApiClient => FlickrApiClient}
import play.api.Play._
import play.api._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.ws.WSClient
import play.api.mvc._

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

  def test = Action.async( implicit request =>
    getFlickrApiClient.checkToken.map(res => res match {
      case Some(tokenInfo) => Ok(tokenInfo.toString)
      case None => InternalServerError("Buu")
    })
  )

  private def getFlickrApiClient(implicit request:RequestHeader) = {
    new FlickrApiClient(current.configuration.getString("alerf.flickr.rest.url").get, apiClient, consumerKey, getRequestToken.get, context)
  }

}
