package controllers

import javax.inject.Inject

import models.flickr.{ApiClient => FlickrApiClient, UserInfo, ResponseParser}
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


  def test = Action.async( implicit request => {
      val fApi = getFlickrApiClient
      val parser = new ResponseParser

      fApi.checkToken.map(_.flatMap(parser.getTokenInfo(_)))

//      fApi.checkToken.flatMap(res => res match {
//        case Some(tokenInfo) => {
//          fApi.getUserPublicFavorites(tokenInfo.nsid, 1, 3)
//          fApi.getUserInfo(tokenInfo.nsid)
//        }
//        case None => Future.successful(None)
//      }).map(res => res match {
//          case Some(userInfo) => Ok(userInfo.toString)
//          case None => InternalServerError("Buu")
//      } )
//    }

    Future { Ok("ok") }
  } )

  private def getFlickrApiClient(implicit request:RequestHeader) = {
    new FlickrApiClient(current.configuration.getString("alerf.flickr.rest.url").get, apiClient, consumerKey, getRequestToken.get, context)
  }

}
