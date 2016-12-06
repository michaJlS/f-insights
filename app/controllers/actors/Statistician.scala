package controllers.actors

import domain.service.Stats
import domain.entities._
import akka.actor.{Actor, Props}
import messages._

import scala.collection.mutable.{HashMap => MHashMap}

class Statistician(stats: Stats) extends Actor with BaseActor {

  override def receive = {
    case msg: DoDashboardStats => calcDashboardStats(msg)
    case msg: DoRelativeStats => calcRelativeStats(msg)
  }

  private def calcDashboardStats(msg: DoDashboardStats): Unit = {
    val photoFavs = msg.photoFavourites.values.flatten.toSeq
    val timeline = stats.monthlyStats(msg.photos, msg.favs, photoFavs)
    val favingUserStats = stats.favingUsers(photoFavs, 0)
    val authors = stats.favsOwnersStats(msg.favs, 0)

    for {
      dbupdater <- context.actorSelection("../dbupdater").resolveOne
    } {
      dbupdater ! LoadedTimeline(msg.dashboardId, timeline)
      dbupdater ! LoadedFavingUsers(msg.dashboardId, favingUserStats.filter(_.count > 2))
    }
  }

  private def calcRelativeStats(msg: DoRelativeStats): Unit = {
    val tagsStats = stats.popularTags(msg.photos)
    val setStats = stats.photoSetStats(msg.photos)
    val topTags = tagsStats.sortBy(_.count).take(20).map(_.tag)

    val relative = Relative(msg.nsid, msg.username, msg.realname, msg.followed,
      msg.faved.size, msg.faving.size, msg.totalContacts, msg.totalPhotos,
      setStats.avgPoints, setStats.topAvgPoints, topTags)

    for {
      dbupdater <- context.actorSelection("../dbupdater").resolveOne
    } {
      dbupdater ! LoadedRelative(msg.dashboardId, relative)
    }
  }

}
