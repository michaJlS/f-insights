package controllers

import play.api._
import play.api.mvc._

class Application extends Controller with Base
{

  def index = Action { implicit request =>
   if (isLogged) {
     Ok(views.html.index("Flickr Assistant."))
   } else {
     TemporaryRedirect(routes.Auth.login.absoluteURL)
   }
  }

}
