package controllers.actions

import play.api.mvc._
import scala.concurrent.Future

class CheckIfMineNsid(nsid:String) extends ActionFilter[RequestWithUserToken]
{

  def filter[A](request: RequestWithUserToken[A]) = Future.successful {
    if (nsid != request.userNsid)
      Some(Results.BadRequest("You can only try to access own dashboards."))
    else
      None
  }

}
