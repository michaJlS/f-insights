package controllers.actors

import akka.actor.{Actor, ActorRef, Props}
import scala.collection.mutable.{Queue => MQueue}
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.Try
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
      case msg: PreloadPhotoFavs => rec(msg, 0)
      case Retry(msg: PreloadPhotoFavs, attempts: Int) => rec(msg, attempts)

      case msg: PreloadFavs => rec(msg, 0)
      case Retry(msg: PreloadFavs, attempts: Int) => rec(msg, attempts)

      case msg: PreloadContacts => rec(msg, 0)
      case Retry(msg: PreloadContacts, attempts: Int) => rec(msg, attempts)

      case msg: PreloadPhotos => rec(msg, 0)
      case Retry(msg: PreloadPhotos, attempts: Int) => rec(msg, attempts)

      case msg: PreloadRelativeContacts => rec(msg, 0)
      case Retry(msg: PreloadRelativeContacts, attempts: Int) => rec(msg, attempts)

      case msg: PreloadRelativePhotos => rec(msg, 0)
      case Retry(msg: PreloadRelativePhotos, attempts: Int) => rec(msg, attempts)
    }

    private def rec(msg: PreloadPhotoFavs, attempts: Int) =
      process(msg, attempts)
        {(m) => repo.getAllPhotoFavs(m.photo, m.owner, m.token)}
        {(m, favs) => loaded(LoadedPhotoFavs(m.dashboardId, m.photo, favs))}

    private def rec(msg: PreloadFavs, attempts: Int) =
      process(msg, attempts)
        {(m) => repo.getAllUserPublicFavoritesSequentially(m.nsid, m.token)}
        {(m, favs) => loaded(LoadedFavs(m.dashboardId, favs))}

    private def rec(msg: PreloadContacts, attempts: Int) =
      process(msg, attempts)
        {(m) => repo.getAllUserPublicContactsSequentially(m.nsid, m.token)}
        {(m, contacts) => loaded(LoadedContacts(m.dashboardId, contacts))}

    private def rec(msg: PreloadPhotos, attempts: Int) =
      process(msg, attempts)
        {(m) => repo.getAllUserPhotosSequentially(m.nsid, m.token)}
        {(m, photos) => {
          loaded(LoadedPhotos(m.dashboardId, photos))
          context.actorSelection("../../preloader").resolveOne.map(_ ! PreloadPhotosFavs(m.token, m.dashboardId, m.nsid, photos.map(_.id)))
        }}

    private def rec(msg: PreloadRelativeContacts, attempts: Int) =
      process(msg, attempts)
        {(m) => repo.getUserPublicContactsSequentially(m.nsid, m.token, 10)}
        {(m, res) => loaded(LoadedRelativeContacts(m.dashboardId, m.nsid, res.total, res.items))}

    private def rec(msg: PreloadRelativePhotos, attempts: Int) =
      process(msg, attempts)
        {(m) => repo.getUserPhotosSequentially(m.nsid, m.token, 20) }
        {(m, res) => loaded(LoadedRelativePhotos(m.dashboardId, m.nsid, res.total, res.items))}

    private def process[A, M](m: M, attempts: Int)(f: M => Future[Option[A]])(g: ((M, A) => Future[Unit])): Unit = {
      Try {
        OptionT(f(m))
          .flatMapF(res => g(m, res))
          .map(_ => iAmReady)
          .orElse(OptionT[Future, Unit](Future.successful(Some(reschedule(m, attempts, "Option")))))
          .run
          .failed.foreach { e => reschedule(m, attempts, "Future", Some(e)) }

      } recover { case e => reschedule(m, attempts, "Exception", Some(e)) }
    }

    private def loaded[T](newMsg: T) = {
      context.actorSelection("../../dbupdater").resolveOne.map(_ ! newMsg)
      context.actorSelection("../../gather").resolveOne.map(_ ! newMsg)
    }

    private def reschedule [M](m: M, attempts: Int, what: String, e: Option[Throwable] = None): Unit = {
      e.foreach(e => logger.info(e.getMessage))
      if (attempts < 10 && what != "Option" || what == "Option" && attempts < 3) {
        logger.info(s"Rescheduling in ${context.self.path.name} a message: $m. Failed $what.")
        context.system.scheduler.scheduleOnce(1.minute, self, Retry(m, attempts + 1))
      } else {
        logger.info(s"Can not process in ${context.self.path.name} a message: $m. Failed $what.")
        context.system.scheduler.scheduleOnce(30.seconds, context.parent, Ready)
      }
    }

    private def iAmReady() = context.parent ! Ready

    override def preStart(): Unit = {
      super.preStart()
      logger.info("(Re)start ! " + context.self.path.name)
      iAmReady
    }

  }

  case class Ready()

  case class Retry[M](m: M, attempts: Int)

}
