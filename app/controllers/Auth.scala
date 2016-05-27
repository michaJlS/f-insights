package controllers

import com.google.inject.Inject
import com.google.inject.name.Named
import play.api._
import play.api.libs.oauth.OAuth
import play.api.mvc._


class Auth @Inject()(@Named("FlickrOAuth") oauth:OAuth) extends Controller with Base
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
