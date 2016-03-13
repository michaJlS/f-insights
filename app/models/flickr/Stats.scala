
package models.flickr


class Stats {

  def tagsStats(photos:Seq[Favourite]) = {
    photos.flatMap(_.photo.allTagsList).groupBy(tag => tag).mapValues(_.length)
  }

  def richTagsStats(photos:Seq[Favourite]): Map[String, (Int, Seq[Favourite])] = {
    photos.
      flatMap(fav => fav.photo.allTagsList.map((t:String) => {(t, fav)})).
      groupBy(_._1).
      mapValues(favs => (favs.length, getTopFavs(favs.map(_._2))))
  }

  def richOwnersStats(photos:Seq[Favourite]) = {
    photos.
      map(fav => (fav.photo.owner, fav.photo.owner_name, fav)).
      groupBy(_._1).
      mapValues(items => (items(0)._1, items(0)._2, items.length, getTopFavs(items.map(_._3).toSeq)))
  }

  def ownersStats(photos:Seq[Favourite]) = {
    photos.map(_.photo.owner).groupBy(author => author).mapValues(_.length)
  }

  private def getTopFavs(favs:Seq[Favourite], n:Int = 10) = favs.sortBy(_.photo.count_faves).reverse.slice(0, n)

}
