package controllers.actions

import play.api.mvc._
import play.api.mvc.{ActionFilter, Result}
import scala.concurrent.Future


class CheckTokenInfo extends ActionFilter[RequestWithTokenInfo]
{

  override protected def filter[A](request: RequestWithTokenInfo[A]): Future[Option[Result]] = {
    if (request.userNsid == request.tokenInfo.nsid)
      Future.successful{None}
    else
      Future.successful{Some(Results.Unauthorized("Provided token is invalid. Token mismatch."))}
  }

}
