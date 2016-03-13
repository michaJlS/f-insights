
package models.flickr


class Stats
{

  def favsTagsStats(photos:Seq[Favourite], threshold:Int = 0, top:Int = 10) = {
    photos.
      flatMap(fav => fav.photo.allTagsList.map((t:String) => {(t, fav)})).
      groupBy(_._1).
      map(mapItem => FavTagStats(mapItem._1, mapItem._2.length, getTopFavs(mapItem._2.map(_._2), top))).
      toSeq.
      filter(_.count >= threshold)
  }

  def favsOwnersStats(photos:Seq[Favourite], threshold:Int = 0, top:Int = 10) = {
    photos.
      map(fav => (fav.photo.owner, fav.photo.owner_name, fav)).
      groupBy(_._1).
      map(mapItem => FavOwnerStats(mapItem._1, mapItem._2(0)._2, mapItem._2.length, getTopFavs(mapItem._2.map(_._3).toSeq, top))).
      toSeq.
      filter(_.count >= threshold)
  }

  private def getTopFavs(favs:Seq[Favourite], n:Int = 10) = favs.sortBy(_.photo.count_faves).reverse.slice(0, n)

}
