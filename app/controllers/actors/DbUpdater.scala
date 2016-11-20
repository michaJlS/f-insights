package controllers.actors

import akka.actor.Actor
import domain.service.AppRepository
import messages._
import play.api.libs.concurrent.Execution.Implicits._

class DbUpdater(appRepo: AppRepository) extends Actor {

  override def receive = {
    case msg: LoadedPhotoFavs => {
      appRepo.insertPhotoFavourites(msg.dashboardId, msg.photoFavs)
    }
  }

}
