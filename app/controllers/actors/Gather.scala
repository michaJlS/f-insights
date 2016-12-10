package controllers.actors

import akka.actor.{Actor, Props}
import java.util.UUID
import scala.collection.mutable.{HashMap => MHashMap}

import domain.entities._
import messages._

class Gather extends Actor with BaseActor {

  override def receive = {
    case msg: BasicDashbaordInfo => context.child("dashboard").map(_.forward(msg))
    case msg: RelativesInfo => context.child("relatives").map(_.forward(msg))
  }

  override def preStart(): Unit = {
    super.preStart()
    createWorkers()
  }

  private def createWorkers() = {
    context.actorOf(Props[Gather.GatherDahsboard], name = "dashboard")
    context.actorOf(Props[Gather.GatherRelatives], name = "relatives")
  }

}

object Gather {

  class GatherDahsboard extends Actor with BaseActor {

    val dashboards = MHashMap.empty[UUID, DashboardData]

    override def receive = {
      case msg: LoadedPhotoFavs => {
        val dashboard = get(msg.dashboardId)
        set(msg.dashboardId, dashboard.copy(photoFavourites = dashboard.photoFavourites + (msg.photo -> msg.photoFavs)))
      }
      case msg: GatherData => set(msg.dashboardId, get(msg.dashboardId).copy(token = Some(msg.token)))
      case msg: LoadedPhotos => set(msg.dashboardId, get(msg.dashboardId).copy(photos = Some(msg.photos)))
      case msg: LoadedContacts => set(msg.dashboardId, get(msg.dashboardId).copy(contacts = Some(msg.contacts)))
      case msg: LoadedFavs => set(msg.dashboardId, get(msg.dashboardId).copy(favs = Some(msg.favs)))
    }

    private def get(id: UUID): DashboardData = dashboards.getOrElse(id, DashboardData(id))

    private def set(id: UUID, data: DashboardData): Unit = {
      dashboards += (id -> data)
      if (dashboardLoaded(id))
        doUserStats(id)
    }

    private def dashboardLoaded(id: UUID): Boolean = dashboards.get(id).map(_.isComplete).getOrElse(false)

    private def doUserStats(id: UUID): Unit = {
      logger.info(s"Loaded dashboard $id , loading stats")
      for {
        dashboard <- dashboards.get(id)
        msg <- dashboard.asMessage
      } {
        context.actorSelection("../../statistician").resolveOne.map(_ ! msg)
        context.actorSelection("../relatives").resolveOne().map(_ ! msg)
      }
      dashboards -= id
    }
  }

  class GatherRelatives extends Actor with BaseActor {
    val relatives = MHashMap.empty[UUID, MHashMap[String, RelativeData]]

    override def receive = {
      case msg: DoDashboardStats => initRelativesForDashboard(msg)
      case msg: LoadedRelativePhotos => update(msg)(_.withPhotos(msg.total, msg.photos))
      case msg: LoadedRelativeContacts => update(msg)(_.withContacts(msg.total, msg.contacts))
    }

    private def update[M <: RelativesInfo](m: M)(f: (RelativeData => RelativeData)): Unit = for {
      d <- relatives.get(m.dashboardId)
      rd <- d.get(m.nsid)
      newRd = f(rd)
    } {
      d += (rd.nsid -> newRd)
      if (newRd.isComplete)
        doRelativeStats(newRd)
    }

    private def doRelativeStats(rd: RelativeData): Unit = {
      for {msg <- rd.asMessage} {
        context.actorSelection("../../statistician").resolveOne.map(_ ! msg)
      }
      for {d <- relatives.get(rd.dashboardId)} {
        d -= rd.nsid
        if (d.size % 50 == 0)
          logger.info(s"# of relatives to collect: ${relatives.size}")
      }
    }

    private def initRelativesForDashboard(msg: DoDashboardStats): Unit = {
      val favedByRelatives = msg.favs.groupBy(_.photo.owner).mapValues(_.toSeq)
      val favingByRelative = msg.photoFavourites.values.flatten.groupBy(_.faved_by).mapValues(_.toSeq)
      val contacts = msg.contacts.map(c => (c.nsid -> c)).toMap

      val r1 = favingByRelative.filter(_._2.size > 2).keys
      val r2 = favedByRelatives.filter(_._2.size > 2).keys

      val nsids = (contacts.keys ++ r1 ++ r2).toSet
      val buff: MHashMap[String, RelativeData] = MHashMap.empty
      relatives += (msg.dashboardId -> buff)

      logger.info(s"To load: ${nsids.size}, in that: ${contacts.size} contacts, ${r1.size} faving and ${r2.size} faved.")

      nsids.foreach(nsid => {
        val faved: Seq[Favourite] = favedByRelatives.getOrElse(nsid, Seq.empty)
        val faving: Seq[PhotoFavourite] = favingByRelative.getOrElse(nsid, Seq.empty)
        val rd = RelativeData(msg.dashboardId, msg.token, nsid, faved, faving, contacts.get(nsid))
        buff += (rd.nsid -> rd)
      })

      context.actorSelection("../../flickrclient").resolveOne.map(fc =>
        buff.values.foreach(rd => {
          fc ! PreloadRelativeContacts(rd.token, rd.dashboardId, rd.nsid)
          fc ! PreloadRelativePhotos(rd.token, rd.dashboardId, rd.nsid)
        })
      )
    }
  }

  case class DashboardData(id: UUID, favs: Option[Seq[Favourite]] = None, contacts: Option[Seq[Contact]] = None,
                            photos: Option[Seq[PhotoExcerpt]] = None,
                            photoFavourites: Map[String, Seq[PhotoFavourite]] = Map.empty,
                            token: Option[UserToken] = None
                          ) {

    def isComplete: Boolean =
      contacts.isDefined && photos.isDefined && favs.isDefined && token.isDefined && photos.map(_.size).getOrElse(-1) == photoFavourites.size

    def asMessage(): Option[DoDashboardStats] =
      for {
        cs <- contacts
        fs <- favs
        ps <- photos
        tn <- token
      } yield DoDashboardStats(tn, id, fs, cs, ps, photoFavourites)
  }

  case class RelativeData(dashboardId: UUID, token: UserToken, nsid: String, realname: String, username: String,
                          faved: Seq[Favourite], faving: Seq[PhotoFavourite],
                          followed: Boolean, contacts: Option[Seq[Contact]] = None, totalContacts: Option[Int] = None,
                          photos: Option[Seq[PhotoExcerpt]] = None, totalPhotos: Option[Int] = None) {

    def isComplete: Boolean = contacts.isDefined && photos.isDefined && totalContacts.isDefined && totalPhotos.isDefined

    def withContacts(total: Int, c: Seq[Contact]) = copy(totalContacts = Some(total), contacts = Some(c))

    def withPhotos(total: Int, c: Seq[PhotoExcerpt]) = copy(totalPhotos = Some(total), photos = Some(c))

    def asMessage(): Option[DoRelativeStats] = for {
      ps <- photos
      tp <- totalPhotos
      cs <- contacts
      tc <- totalContacts
    } yield DoRelativeStats(token, dashboardId, nsid, realname, username, followed, faved, faving, cs, tc, ps, tp)

  }

  object RelativeData {
    def apply(dashboardId: UUID, token: UserToken, nsid: String, faved: Seq[Favourite],
              faving: Seq[PhotoFavourite], followed: Option[Contact]): RelativeData = {
      val realname = faving.headOption.map(_.realname).getOrElse("")
      val username = Seq[Option[String]](
        followed.map(_.username),
        faving.headOption.map(_.username),
        faved.headOption.map(_.photo.owner_name)
      ).collectFirst({case Some(s) => s})
        .getOrElse("")

      RelativeData(dashboardId, token, nsid, realname, username, faved, faving, followed.isDefined)
    }

  }

}