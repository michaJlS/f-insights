package controllers.actions

import play.api.mvc._
import scala.concurrent.Future

class CheckNsid(nsid:String) extends  ActionFilter[Request]
{

  def filter[A](request: Request[A]) = Future.successful {
    if (nsid.length == 0)
      Some(Results.BadRequest("Provided `nsid` is empty."))
    else
      None
  }

}
