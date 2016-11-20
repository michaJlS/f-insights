package domain.service

import domain.entities._

class Stats
{

  def favingUsers(photoFavs: Seq[PhotoFavourite], threshold: Int = 3): Seq[FavingUserStats] =
    photoFavs
      .groupBy(_.faved_by)
      .map { case (by, favs) => {
          val fav = favs(0)
          FavingUserStats(by, fav.username, fav.realname, favs.size)
      } }
      .toSeq
      .filter(_.count >= threshold)

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

    stats.toSeq.sorted
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

  def favsTagsStats(photos: Seq[Favourite], threshold: Int = 0, top: Int = 10) =
    photos
      .flatMap(fav => fav.photo.allTagsList.map((t:String) => {(t, fav)}))
      .groupBy(_._1)
      .mapValues(_.map(_._2))
      .map { case (tag, favs) => FavTagStats(tag, favs.size, getTopFavs(favs, top)) }
      .toSeq
      .filter(_.count >= threshold)

  def favsOwnersStats(photos: Seq[Favourite], threshold: Int = 0, top: Int = 10) =
    photos
      .groupBy(_.photo.owner)
      .map { case (owner, favs) => {
        val photo = favs(0).photo
        FavOwnerStats(owner, photo.owner_name, favs.size, getTopFavs(favs, top))
      } }
      .toSeq
      .filter(_.count >= threshold)

  private def getTopFavs(favs: Seq[Favourite], n: Int = 10) = favs.sortBy(_.photo.count_faves).reverse.slice(0, n)

  private def getTopPhotosByPoints(photos: Seq[PhotoExcerpt], n: Int = 0): Seq[PhotoExcerpt] = photos.sortBy(_.points).reverse.slice(0, n)

  private def avgPoints(photos: Seq[PhotoExcerpt]): Double = photos.foldLeft[Double](0.0)({
    case (total, photo) => total + photo.points
  }) / photos.size

  private def byMonth[T](s: Seq[T], f:(T => String)): Map[String, Int] = s.map(f).groupBy(t => t).mapValues(_.size)

}
