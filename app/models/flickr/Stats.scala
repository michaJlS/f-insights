
package models.flickr


class Stats {

  def tagsStats(photos:Seq[PhotoExcerpt]) = {
    photos.flatMap(_.allTagsList).groupBy(tag => tag).mapValues(_.length)
  }

  def ownersStats(photos:Seq[PhotoExcerpt]) = {
    photos.map(_.owner).groupBy(author => author).mapValues(_.length)
  }

}
