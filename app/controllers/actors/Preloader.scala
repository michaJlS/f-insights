package controllers.actors

import java.util.concurrent.TimeUnit

import akka.actor.Actor
import akka.util.Timeout
import messages._

class Preloader extends Actor
{

  import context.dispatcher

  implicit val timeout = new Timeout(1, TimeUnit.SECONDS)

  override def receive: Receive = {
    case msg: PreloadDashboard => {
      for {
        flickrClient <- context.actorSelection("../flickrclient").resolveOne
        statistician <- context.actorSelection("../statistician").resolveOne
      } {
        flickrClient ! PreloadFavs(msg.token, msg.dashboardId, msg.nsid)
        flickrClient ! PreloadContacts(msg.token, msg.dashboardId, msg.nsid)
        flickrClient ! PreloadPhotos(msg.token, msg.dashboardId, msg.nsid)
        statistician ! DoStats(msg.token, msg.dashboardId)
      }

    }

    case msg: PreloadPhotosFavs => {
      for {
        flickrClient <- context.actorSelection("../flickrclient").resolveOne
        p <- msg.photos
      } {
        flickrClient ! PreloadPhotoFavs(msg.token, msg.dashboardId, msg.owner, p)
      }
    }
  }

}
