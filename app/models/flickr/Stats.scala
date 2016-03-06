
package models.flickr


class Stats {

  def tagsStats(photos:Seq[Favourite]) = {
    photos.flatMap(_.photo.allTagsList).groupBy(tag => tag).mapValues(_.length)
  }

  def ownersStats(photos:Seq[Favourite]) = {
    photos.map(_.photo.owner).groupBy(author => author).mapValues(_.length)
  }

}
