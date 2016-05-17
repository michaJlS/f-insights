package controllers.actions

import models.flickr.ApiRepository
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class AttachTokenInfo(repository:ApiRepository)(implicit executor:ExecutionContext) extends ActionRefiner[RequestWithUserToken, RequestWithTokenInfo]
{

  override protected def refine[A](request: RequestWithUserToken[A]): Future[Either[Result, RequestWithTokenInfo[A]]] = {
      repository
        .checkToken(request.token)
        .map {
          case None => Left(Results.Unauthorized("Provided token is invalid."))
          case Some(ti) => Right(new RequestWithTokenInfo[A](ti, request))
        }
  }

}
