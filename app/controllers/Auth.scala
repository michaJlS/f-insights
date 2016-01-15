package controllers

import play.api._
import play.api.mvc._

/**
 *
 */
class Auth extends Controller with Base with Flickr
{

  def login = Action { implicit request =>
    oauth.retrieveRequestToken(routes.Auth.logon.absoluteURL) match {
      case Right(t) => withToken(TemporaryRedirect(oauth.redirectUrl(t.token)), t, "request")
      case _ => InternalServerError("Could not retrieve a request token.")
    }
  }

  def logon = Action { implicit request =>

    val accessToken = for {
      verifier <- request.getQueryString("oauth_verifier")
      token <- getRequestToken("request")
    } yield oauth.retrieveAccessToken(token, verifier)

    accessToken match {
      case Some(Right(t)) => withToken(TemporaryRedirect(routes.Application.index.absoluteURL), t, "access")
      case _ => InternalServerError("Could not retrieve an access token")
    }
  }

  def logout = Action { implicit request =>
    TemporaryRedirect(routes.Application.index.absoluteURL()).withNewSession
  }

}
