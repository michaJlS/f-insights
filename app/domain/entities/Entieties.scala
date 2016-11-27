package domain.entities

import java.util.UUID

import org.joda.time.DateTime
import utils.TimeX

case class UserToken(token:String, secret:String)

case class TokenInfo(nsid: String, username: String, fullname: String)

case class UserInfo(id:String, nsid:String, username: String, fullname: String, photosurl: String, uploads:Int, firstupload:String)

case class AppUserDetail(nsid:String, detail_key:String, detail_value:String)

case class Contact(nsid:String, username:String, contactOf:String)

case class PhotoExcerpt(id:String, title:String, owner:String, owner_name:String, date_upload:String, date_taken:String, count_views:Int, count_faves:Int, count_comments:Int, tags:String, machine_tags:String, urls:PhotoUrls) {

  val points: Double = count_faves + count_comments * 1.5

  val month_upload: String = TimeX.unixToYM(date_upload)

  lazy val tagsList = {parseTags(tags)}

  lazy val machineTagsList = {parseTags(machine_tags)}

  lazy val allTagsList = {(tagsList ++ machineTagsList).distinct}

  private def parseTags(t:String) = {t.split(" ").filter(_ != "")}

}

object PhotoExcerpt {
  val medtiaTypePhoto = "photo"
}

case class Favourite(photo: PhotoExcerpt, faved_by: String, date_faved:String) {
  val month_faved = TimeX.unixToYM(date_faved)
}

case class PhotoFavourite(photoId: String, owner: String, faved_by: String, username: String, realname: String, date_faved: String) {
  val month_faved = TimeX.unixToYM(date_faved)
}

case class CollectionInfo(page:Int, pages:Int, perPage: Int, total: Int)

case class FavouritesColectionInfo()

// https://www.flickr.com/services/api/misc.urls.html
case class PhotoUrls(largeSquareThumb:String = "", largeThumb:String = "", small:String = "", large:String = "") {
  def toMap = Map("large_square_thumb" -> largeSquareThumb, "large_thumb" -> largeThumb, "small" -> small, "large" -> large)
}

object PhotoUrls {

  def apply(m:Map[String, String]):PhotoUrls = PhotoUrls(
    largeSquareThumb = m.getOrElse("large_square_thumb", ""),
    largeThumb = m.getOrElse("large_thumb", ""),
    small = m.getOrElse("small", ""),
    large = m.getOrElse("large", "")
  )

}

case class Dashboard(nsid: String, id:UUID, created_at: DateTime)

case class FavTagStats(tag: String, count: Int, photos:Seq[PhotoExcerpt])

case class FavOwnerStats(owner: String, owner_name: String, count: Int, photos:Seq[PhotoExcerpt])

case class PhotoTagStats(tag: String, count: Int, avgPoints: Double, topAvgPoints: Double, photos: Seq[PhotoExcerpt])

case class FavingUserStats(user: String, username: String, realname: String, count: Int, firstFav: String, lastFav: String)

case class MonthlyStats(month: String, uploaded: Int = 0, faved: Int = 0, gotFavs: Int = 0)

case class Relative(nsid: String, username: String, realname: String, followed: Boolean, faved: Int, faving: Int,
                    contacts: Int, photos: Int, avgPoints: Double, topAvgPoints: Double, topTags: Seq[String])

case class PhotoSetStats(avgPoints: Double, topAvgPoints: Double)