package controllers

import play.api.libs.oauth.RequestToken
import play.api.mvc.RequestHeader

/**
 *
 */
trait Base 
{

  protected def isLogged(implicit request: RequestHeader):Boolean = {
    getRequestToken.isDefined
  }

  protected def getRequestToken(implicit request: RequestHeader): Option[RequestToken] = {
    for {
      token <- request.session.get("token")
      secret <- request.session.get("secret")
    } yield RequestToken(token, secret)
  }

}
