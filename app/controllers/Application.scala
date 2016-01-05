package controllers

import java.util.Calendar
import javax.inject.Inject

import play.api._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.ws.WSClient
import play.api.mvc._
import play.api.Play._


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
   Future { Ok("nanana") }
  } )




}
