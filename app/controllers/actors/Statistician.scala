package controllers.actors

import domain.service.Stats
import domain.entities._
import akka.actor.{Actor, Props}
import messages._
import java.util.UUID
import java.util.concurrent.TimeUnit

import akka.util.Timeout
import controllers.actors.Statistician.{DashboardData, RelativeData}

import scala.collection.mutable.{HashMap => MHashMap}

class Statistician(stats: Stats) extends Actor {
  implicit val timeout = new Timeout(1, TimeUnit.SECONDS)
  import play.api.libs.concurrent.Execution.Implicits._

  val dashboards = MHashMap.empty[UUID, DashboardData]
  val relatives = MHashMap.empty[UUID, MHashMap[String, RelativeData]]
  val tokens = MHashMap.empty[UUID, UserToken]

  override def receive = {
    case msg: LoadedPhotoFavs => {
      val dashboard = get(msg.dashboardId)
      set(msg.dashboardId, dashboard.copy(photoFavourites = dashboard.photoFavourites + (msg.photo -> msg.photoFavs)))
    }
    case msg: DoStats => set(msg.dashboardId, get(msg.dashboardId).copy(token = Some(msg.token)))
    case msg: LoadedPhotos => set(msg.dashboardId, get(msg.dashboardId).copy(photos = Some(msg.photos)))
    case msg: LoadedContacts => set(msg.dashboardId, get(msg.dashboardId).copy(contacts = Some(msg.contacts)))
    case msg: LoadedFavs => set(msg.dashboardId, get(msg.dashboardId).copy(favs = Some(msg.favs)))
    case msg: LoadedRelativePhotos =>
      for {
        coll <- relatives.get(msg.dashboardId)
        rd <- coll.get(msg.nsid)
      } {
        val tagsStats = stats.popularTags(msg.photos)
        val setStats = stats.photoSetStats(msg.photos)
        val newRd = rd.copy(
          photos = Some(msg.total),
          avgPoints = Some(setStats.avgPoints),
          topAvgPoints = Some(setStats.topAvgPoints),
          topTags = Some(tagsStats.sortBy(_.count).take(20).map(_.tag))
        )
        coll += (msg.nsid -> newRd)
        if (newRd.isComplete)
          doRelativeStats(newRd)
      }
    case msg: LoadedRelativeContacts =>
      for {
        coll <- relatives.get(msg.dashboardId)
        rd <- coll.get(msg.nsid)
      } {
        val newRd = rd.copy(contacts = Some(msg.total))
        coll += (msg.nsid -> newRd)
        if (newRd.isComplete)
          doRelativeStats(newRd)
      }
  }

  private def get(id: UUID): DashboardData = dashboards.getOrElse(id, DashboardData())

  private def set(id: UUID, data: DashboardData): Unit = {
    dashboards += (id -> data)
    if (dashboardLoaded(id))
      doUserStats(id)
  }

  private def dashboardLoaded(id: UUID): Boolean =
    dashboards.contains(id) && dashboards.get(id).map(_.isComplete).getOrElse(false)

  private def doUserStats(id: UUID): Unit = {
    for {
      dashboard <- dashboards.get(id)
      contacts <- dashboard.contacts
      favs <- dashboard.favs
      photos <- dashboard.photos
      token <- dashboard.token
    } {
      val photoFavs = dashboard.photoFavourites.values.flatten.toSeq

      val timeline = stats.monthlyStats(photos, favs, photoFavs)

      val favingUserStats = stats.favingUsers(photoFavs, 0)
      val authors = stats.favsOwnersStats(favs, 0)

      for {
        dbupdater <- context.actorSelection("../dbupdater").resolveOne
      } {
        dbupdater ! LoadedTimeline(id, timeline)
        dbupdater ! LoadedFavingUsers(id, favingUserStats.filter(_.count > 2))
      }

      loadRelatives(id, token, contacts, favingUserStats, authors)
    }
    dashboards -= id
  }

  private def loadRelatives(id: UUID, token: UserToken, contacts: Seq[Contact], favingUserStats: Seq[FavingUserStats], authors: Seq[FavOwnerStats]) = {
    val favingMap = favingUserStats.map(x => x.user -> x).toMap
    val contactMap = contacts.map(x => x.nsid -> x).toMap
    val authorsMap = authors.map(x => x.owner -> x).toMap

    if (!relatives.contains(id)) {
      relatives += (id -> MHashMap.empty)
    }

    val relativesIds =  favingMap.keySet ++ contactMap.keySet ++ authorsMap.keySet

    relativesIds.map(nsid => {
      val realname = Seq(
        favingMap.get(nsid).map(_.realname)
      ).collectFirst({case Some(s) => s})
        .getOrElse("")
      val username = Seq(
        favingMap.get(nsid).map(_.username),
        contactMap.get(nsid).map(_.username),
        authorsMap.get(nsid).map(_.owner_name)
      ).collectFirst({case Some(s) => s}).getOrElse("")
      val faved = authorsMap.get(nsid).map(_.count).getOrElse(0)
      val faving = favingMap.get(nsid).map(_.count).getOrElse(0)
      val followed = contactMap.contains(nsid)
      if (followed || faved > 2 || faving > 2) {
        relatives.get(id).map(_ += (nsid -> RelativeData(id, token, nsid, realname, username, faved, faving, followed)))
        context.actorSelection("../flickrclient").resolveOne.map(fc => {
          fc ! PreloadRelativeContacts(token, id, nsid)
          fc ! PreloadRelativePhotos(token, id, nsid)
        })
      }
    })
  }

  private def doRelativeStats(rd: RelativeData): Unit =
    for {
      coll <- relatives.get(rd.dashboardId)
    } {
      context.actorSelection("../dbupdater").resolveOne.map(_ ! LoadedRelative(rd.dashboardId, rd.asRelative))
      coll -= rd.nsid
    }

}

object Statistician {

  case class DashboardData(
    favs: Option[Seq[Favourite]] = None,
    contacts: Option[Seq[Contact]] = None,
    photos: Option[Seq[PhotoExcerpt]] = None,
    photoFavourites: Map[String, Seq[PhotoFavourite]] = Map.empty,
    token: Option[UserToken] = None
  ) {

    def isComplete: Boolean =
      contacts.isDefined && photos.isDefined && favs.isDefined && token.isDefined && photos.map(_.size).getOrElse(-1) == photoFavourites.size

  }

  case class RelativeData(dashboardId: UUID, token: UserToken, nsid: String, realname: String, username: String, faved: Int,
                          faving: Int, followed: Boolean, contacts: Option[Int] = None, photos: Option[Int] = None,
                          avgPoints: Option[Double] = None, topAvgPoints: Option[Double] = None,
                          topTags: Option[Seq[String]] = None) {

    def isComplete: Boolean =
      contacts.isDefined && photos.isDefined && avgPoints.isDefined && topAvgPoints.isDefined

    def asRelative: Relative = Relative(nsid, username, realname, followed, faved, faving, contacts.getOrElse(0),
      photos.getOrElse(0), avgPoints.getOrElse(0), topAvgPoints.getOrElse(0), topTags.getOrElse(Seq.empty))

  }

}
