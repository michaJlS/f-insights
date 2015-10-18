package controllers

import play.api._
import play.api.mvc._

class Application extends Controller with Base
{

  def index = Action { implicit request =>
   if (isLogged) {
     Ok(views.html.index("Your new application is ready."))
   } else {
     TemporaryRedirect(routes.Auth.login.absoluteURL)
   }
  }

}
