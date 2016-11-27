package controllers.actors

import akka.actor.Actor
import domain.service.AppRepository
import messages._
import play.api.libs.concurrent.Execution.Implicits._

class DbUpdater(appRepo: AppRepository) extends Actor {

  override def receive = {
    case msg: LoadedPhotoFavs =>  appRepo.insertPhotoFavourites(msg.dashboardId, msg.photoFavs)
    case msg: LoadedPhotos => appRepo.insertUserPhotos(msg.dashboardId, msg.photos)
    case msg: LoadedContacts => appRepo.insertContacts(msg.dashboardId, msg.contacts)
    case msg: LoadedFavs => appRepo.insertFavourties(msg.dashboardId, msg.favs)
    case msg: LoadedTimeline => appRepo.insertTimeline(msg.dashboardId, msg.timeline)
    case msg: LoadedFavingUsers => appRepo.insertFavingUsers(msg.dashboardId, msg.favingUserStats)
    case msg: LoadedRelativeContacts => appRepo.insertContacts(msg.dashboardId, msg.contacts)
    case msg: LoadedRelativePhotos => appRepo.insertUserPhotos(msg.dashboardId, msg.photos)
    case msg: LoadedRelative => appRepo.insertRelative(msg.dashboardId, msg.relative)
  }

}
