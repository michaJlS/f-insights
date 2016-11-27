package controllers.actors

import akka.actor.{Actor, Props}
import controllers.actors.messages.{PreloadDashboard, PreloadPhotosFavs}
import domain.service.{AppRepository, Stats}
import infrastructure.flickr.ApiRepository

class Manager(appRepo: AppRepository, apiRepo: ApiRepository, stats: Stats) extends Actor {

  val dbUpdaterProps = Props(new DbUpdater(appRepo))
  val preloaderProps = Props(classOf[Preloader])
  val flickrClientProps = Props(new FlickrClient(apiRepo))
  val statisticianProps = Props(new Statistician(stats))

  override def preStart(): Unit = {
    super.preStart()
    context.actorOf(dbUpdaterProps, name = "dbupdater")
    context.actorOf(preloaderProps, name = "preloader")
    context.actorOf(flickrClientProps, name = "flickrclient")
    context.actorOf(statisticianProps, name = "statistician")
  }

  override def receive = {
    case msg: PreloadPhotosFavs => context.child("preloader").map(_.forward(msg))
    case msg: PreloadDashboard =>  context.child("preloader").map(_.forward(msg))
  }

}
