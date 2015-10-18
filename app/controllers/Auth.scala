package controllers

import play.api.mvc.Controller
import play.api._
import play.api.mvc._

/**
 *
 */
class Auth extends Controller with Base
{

  def login = Action { implicit request =>
    InternalServerError("Not yet implemented")
  }

  def logon = Action { implicit request =>
    InternalServerError("Not yet implemented")
  }

  def logout = Action { implicit request =>
    InternalServerError("Not yet implemented")
  }

}
