package controllers.actors

import akka.actor.{Actor, ActorRef, Props}
import scala.collection.mutable.{Queue => MQueue}
import scalaz._
import Scalaz._

import infrastructure.flickr.ApiRepository
import messages._


class FlickrClient(apiRepo: ApiRepository, parallelism: Int = 4) extends Actor{

  import FlickrClient._

  val availableWorkers: MQueue[ActorRef] = MQueue.empty
  val workBuffer: MQueue[PreloadTask] = MQueue.empty

  override def receive = {
    case Ready => {
      if (workBuffer.isEmpty)
        availableWorkers += sender
      else
        sender ! workBuffer.dequeue
    }
    case msg: PreloadTask => {
      if (availableWorkers.isEmpty)
        workBuffer += msg
      else
        availableWorkers.dequeue ! msg
    }
  }

  override def preStart(): Unit = {
    super.preStart()
    createWorkers
  }

  private def createWorkers() = {
    val props = Props(new Worker(apiRepo))
    for (i <- 1 to parallelism) {
      context.actorOf(props, name = "flickrclientworker" + i)
    }
  }

}

object FlickrClient {

  class Worker(repo: ApiRepository) extends Actor with BaseActor {

    override def receive = {
      case msg: PreloadPhotoFavs =>
        OptionT(repo.getAllPhotoFavs(msg.photo, msg.owner, msg.token))
          .flatMapF { favs => loaded(LoadedPhotoFavs(msg.dashboardId, msg.photo, favs)) }
          .map { _ => iAmReady }
          .run
      case msg: PreloadFavs =>
        OptionT(repo.getAllUserPublicFavoritesSequentially(msg.nsid, msg.token))
          .flatMapF { favs => loaded(LoadedFavs(msg.dashboardId, favs)) }
          .map { _ => iAmReady }
          .run
      case msg: PreloadContacts =>
        OptionT(repo.getAllUserPublicContactsSequentially(msg.nsid, msg.token))
          .flatMapF { contacts => loaded(LoadedContacts(msg.dashboardId, contacts)) }
          .map { _ => iAmReady }
          .run
      case msg: PreloadPhotos =>
        OptionT(repo.getAllUserPhotosSequentially(msg.nsid, msg.token))
          .flatMapF(photos => {
            loaded(LoadedPhotos(msg.dashboardId, photos))
            context.actorSelection("../../preloader").resolveOne.map(_ ! PreloadPhotosFavs(msg.token, msg.dashboardId, msg.nsid, photos.map(_.id)))
          })
          .map { _ => iAmReady }
          .run
      case msg: PreloadRelativeContacts =>
        OptionT(repo.getUserPublicContactsSequentially(msg.nsid, msg.token, 10))
          .flatMapF { res => loaded(LoadedRelativeContacts(msg.dashboardId, msg.nsid, res.total, res.items)) }
          .map { _ => iAmReady }
          .run
      case msg: PreloadRelativePhotos =>
        OptionT(repo.getUserPhotosSequentially(msg.nsid, msg.token, 20))
          .flatMapF { res => loaded(LoadedRelativePhotos(msg.dashboardId, msg.nsid, res.total, res.items)) }
          .map { _ => iAmReady }
          .run

    }

    private def loaded[T](newMsg: T) = {
      context.actorSelection("../../dbupdater").resolveOne.map(_ ! newMsg)
      context.actorSelection("../../statistician").resolveOne.map(_ ! newMsg)
    }

    private def iAmReady() = context.parent ! Ready

    override def preStart(): Unit = {
      super.preStart()
      iAmReady
    }

  }

  case class Ready()

}
