package controllers.actors

import akka.actor.{Actor, Props}
import controllers.actors.messages.PreloadPhotosFavs
import domain.service.AppRepository
import infrastructure.flickr.ApiRepository

class Manager(appRepo: AppRepository, apiRepo: ApiRepository) extends Actor {

  val dbUpdaterProps = Props(new DbUpdater(appRepo))
  val preloaderProps = Props(classOf[Preloader])
  val flickrClientProps = Props(new FlickrClient(apiRepo))

  override def preStart(): Unit = {
    super.preStart()
    context.actorOf(dbUpdaterProps, name = "dbupdater")
    context.actorOf(preloaderProps, name = "preloader")
    context.actorOf(flickrClientProps, name = "flickrclient")
  }

  override def receive = {
    case msg: PreloadPhotosFavs => context.child("preloader").map(_.forward(msg))
  }

}
