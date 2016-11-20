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
    case msg: PreloadPhotosFavs => {
      for {
        flickrClient <- context.actorSelection("../flickrclient").resolveOne
        dbupdater <- context.actorSelection("../dbupdater").resolveOne
        p <- msg.photos
      } {
        flickrClient ! PreloadPhotoFavs(dbupdater, msg.token, msg.dashboardId, msg.owner, p)
      }
    }
  }

}
