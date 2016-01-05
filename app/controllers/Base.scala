package controllers

import play.api.libs.oauth.RequestToken
import play.api.mvc.{Result, RequestHeader}

/**
 *
 */
trait Base 
{

  protected def isLogged(implicit request: RequestHeader):Boolean = {
    getRequestToken("access").isDefined
  }

  protected def getRequestToken(prefix:String)(implicit request: RequestHeader): Option[RequestToken] = {
    for {
      token <- request.session.get(prefix + "_token")
      secret <- request.session.get(prefix + "_secret")
    } yield RequestToken(token, secret)
  }

  protected def withToken(result:Result, token:RequestToken, prefix:String) = {
    result.withSession(
      (prefix + "_token") -> token.token,
      (prefix + "_secret") -> token.secret
    )
  }

}
