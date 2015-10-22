package controllers

import play.api.mvc.Controller
import play.api._
import play.api.mvc._

/**
 *
 */
class Auth extends Controller with Base with Flickr
{

  def login = Action { implicit request =>
    // Actually it does a request to flickr api, so maybe it should be wrapped in future?
    oauth.retrieveRequestToken(routes.Auth.logon.absoluteURL) match {
      case Right(token) => TemporaryRedirect(oauth.redirectUrl(token.token)).withSession("token" -> token.token, "secret" -> token.secret)
      case Left(_) => InternalServerError("Could not retrieve a request token.")
    }
  }

  def logon = Action { implicit request =>

    val accessToken = for {
      verifier <- request.getQueryString("oauth_verifier")
      token <- getRequestToken
    } yield oauth.retrieveAccessToken(token, verifier)
    // the same case like above - we are doing here a request to flickr api

    accessToken match {
      case Some(Right(token)) => TemporaryRedirect(routes.Application.index.absoluteURL).withSession("token" -> token.token, "secret" -> token.secret)
      case _ => InternalServerError("Could not retrieve an access token")
    }
  }

  def logout = Action { implicit request =>
    InternalServerError("Not yet implemented")
  }

}
