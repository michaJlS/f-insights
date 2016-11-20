package controllers.actors
import akka.actor.{Actor, ActorRef, Props}
import infrastructure.flickr.ApiRepository

import scala.collection.mutable.{Queue => MQueue}
import messages.{LoadedPhotoFavs, PreloadPhotoFavs}

import scalaz._
import Scalaz._

class FlickrClient(apiRepo: ApiRepository, parallelism: Int = 4) extends Actor{

  import FlickrClient._

  val availableWorkers: MQueue[ActorRef] = MQueue.empty

  val workBuffer: MQueue[PreloadPhotoFavs] = MQueue.empty

  override def receive = {
    case Ready => {
      if (workBuffer.isEmpty)
        availableWorkers += sender
      else
        sender ! workBuffer.dequeue
    }
    case msg: PreloadPhotoFavs => {
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

  class Worker(repo: ApiRepository) extends Actor{

    import play.api.libs.concurrent.Execution.Implicits._

    override def receive = {
      case msg: PreloadPhotoFavs => {
        OptionT(repo.getAllPhotoFavs(msg.photo, msg.owner, msg.token))
          .map { favs => msg.replyTo ! LoadedPhotoFavs(msg.dashboardId, favs) }
          .map { _ => iAmReady }
          .run
      }
    }

    private def iAmReady() = context.parent ! Ready

    override def preStart(): Unit = {
      super.preStart()
      iAmReady
    }

  }

  case class Ready()

}
