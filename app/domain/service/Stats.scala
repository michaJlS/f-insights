package domain.service

import domain.entities._

class Stats
{

  def uploadedByMonth(photos: Seq[PhotoExcerpt]) =
    photos
      .map(p => p.month_upload)
      .groupBy(t => t)
      .mapValues(_.size)

  def favedByMonth(favs: Seq[Favourite]) =
    favs
    .map(_.month_faved)
    .groupBy(t => t)
    .mapValues(_.size)

  def popularTags(photos: Seq[PhotoExcerpt], threshold: Int = 3, top: Int = 10) =
    photos
      .flatMap(photo => photo.allTagsList.map(t => (t, photo)))
      .groupBy(_._1)
      .mapValues(_.map(_._2))
      .map { case (tag, ps) =>
          val topPs = getTopPhotosByPoints(ps, top)
          PhotoTagStats(tag, photos.size, avgPoints(ps), avgPoints(topPs), topPs)
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
      .map(fav => (fav.photo.owner, fav.photo.owner_name, fav))
      .groupBy(_._1)
      .map(mapItem => FavOwnerStats(mapItem._1, mapItem._2(0)._2, mapItem._2.length, getTopFavs(mapItem._2.map(_._3).toSeq, top)))
      .toSeq
      .filter(_.count >= threshold)

  private def getTopFavs(favs: Seq[Favourite], n: Int = 10) = favs.sortBy(_.photo.count_faves).reverse.slice(0, n)

  private def getTopPhotosByPoints(photos: Seq[PhotoExcerpt], n: Int = 0): Seq[PhotoExcerpt] = photos.sortBy(_.points).reverse.slice(0, n)

  private def avgPoints(photos: Seq[PhotoExcerpt]): Double = photos.foldLeft[Double](0.0)({
    case (total, photo) => total + photo.points
  }) / photos.size

}
