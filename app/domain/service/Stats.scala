package domain.service

import domain.entities._

class Stats
{

  def photoSetStats(photos: Seq[PhotoExcerpt], topSize: Int = 10): PhotoSetStats = {
    val top = getTopPhotosByPoints(photos, topSize)
    PhotoSetStats(photos.map(_.points).sum / photos.size, top.map(_.points).sum / top.size)
  }

  def favingUsers(photoFavs: Seq[PhotoFavourite], threshold: Int = 3): Seq[FavingUserStats] =
    photoFavs
      .groupBy(_.faved_by)
      .map { case (by, favs) => {
          val fav = favs(0)
          val first = favs.minBy(_.date_faved).date_faved
          val last = favs.maxBy(_.date_faved).date_faved
          FavingUserStats(by, fav.username, fav.realname, favs.size, first, last)
      } }
      .toSeq
      .filter(_.count >= threshold)
      .sortBy(_.count * -1)

  def monthlyStats(
                    photos: Seq[PhotoExcerpt],
                    favs: Seq[Favourite],
                    gotFavs: Seq[PhotoFavourite]
                  ): Seq[MonthlyStats] = {
    val uploaded = uploadedByMonth(photos)
    val faved = favedByMonth(favs)
    val received = gotFavByMonth(gotFavs)

    val stats = for {k <- uploaded.keySet ++ faved.keySet ++ received.keySet}
      yield MonthlyStats(k, uploaded.getOrElse(k, 0), faved.getOrElse(k, 0), received.getOrElse(k, 0))

    stats.toSeq.sortBy(_.month)
  }

  def uploadedByMonth(photos: Seq[PhotoExcerpt]): Map[String, Int] = byMonth(photos, (p:PhotoExcerpt) => p.month_upload)

  def favedByMonth(favs: Seq[Favourite]): Map[String, Int] = byMonth(favs, (f:Favourite) => f.month_faved)

  def gotFavByMonth(favs: Seq[PhotoFavourite]): Map[String, Int] = byMonth(favs, (f:PhotoFavourite) => f.month_faved)

  def popularTags(photos: Seq[PhotoExcerpt], threshold: Int = 3, top: Int = 10) =
    photos
      .flatMap(photo => photo.allTagsList.map(t => (t, photo)))
      .groupBy(_._1)
      .mapValues(_.map(_._2))
      .map { case (tag, ps) =>
          val topPs = getTopPhotosByPoints(ps, top)
          PhotoTagStats(tag, ps.size, avgPoints(ps), avgPoints(topPs), topPs)
      }
      .toSeq
      .filter(_.count> threshold)
      .sortBy(_.topAvgPoints * -1)

  def favsTagsStats(photos: Seq[Favourite], threshold: Int = 0, top: Int = 10) =
    photos
      .flatMap(fav => fav.photo.allTagsList.map((t:String) => {(t, fav.photo)}))
      .groupBy(_._1)
      .mapValues(_.map(_._2))
      .map { case (tag, photos) => FavTagStats(tag, photos.size, getTopFavs(photos, top)) }
      .toSeq
      .filter(_.count >= threshold)
      .sortBy(_.count * -1)

  def favsOwnersStats(photos: Seq[Favourite], threshold: Int = 0, top: Int = 10) =
    photos
      .map(_.photo)
      .groupBy(_.owner)
      .map { case (owner, photos) => {
        val photo = photos(0)
        FavOwnerStats(owner, photo.owner_name, photos.size, getTopFavs(photos, top))
      } }
      .toSeq
      .filter(_.count >= threshold)
      .sortBy(_.count * -1)

  private def getTopFavs(photos: Seq[PhotoExcerpt], n: Int = 10) = photos.sortBy(_.count_faves * -1).slice(0, n)

  private def getTopPhotosByPoints(photos: Seq[PhotoExcerpt], n: Int = 0): Seq[PhotoExcerpt] = photos.sortBy(_.points * -1).slice(0, n)

  private def avgPoints(photos: Seq[PhotoExcerpt]): Double =
    if (photos.size > 0)
      photos.foldLeft[Double](0.0)({
        case (total, photo) => total + photo.points
      }) / photos.size
    else
      0

  private def byMonth[T](s: Seq[T], f:(T => String)): Map[String, Int] = s.map(f).groupBy(t => t).mapValues(_.size)

}
