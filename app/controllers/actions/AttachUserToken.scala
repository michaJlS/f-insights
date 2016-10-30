package controllers.actions

import domain.entities.UserToken
import play.api.mvc._

import scala.concurrent.Future


class AttachUserToken(tokenHeader:String = "fa_token", secretHeader:String = "fa_secret", nsidHeader:String = "fa_nsid") extends ActionRefiner[Request, RequestWithUserToken] {

  override protected def refine[A](request: Request[A]): Future[Either[Result, RequestWithUserToken[A]]] = {

    val token = for {
      t <- request.headers.get(tokenHeader)
      s <- request.headers.get(secretHeader)
    } yield UserToken(t, s)

    val userNsid = request.headers.get(nsidHeader)

    if (token.isEmpty || userNsid.isEmpty)
      Future.successful(Left(Results.BadRequest("Token details have not been provided.")))
    else
      Future.successful(Right(new RequestWithUserToken[A](token.get, userNsid.get, request)))

  }

}
