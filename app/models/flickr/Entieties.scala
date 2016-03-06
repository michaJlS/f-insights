package models.flickr

import java.util.UUID
import org.joda.time.DateTime
/**
 *
 */

case class UserToken(token:String, secret:String)

case class TokenInfo(nsid: String, username: String, fullname: String)

case class UserInfo(id:String, nsid:String, username: String, fullname: String, photosurl: String, uploads:Int, firstupload:String)

case class AppUserDetail(nsid:String, detail_key:String, detail_value:String)

case class PhotoExcerpt(id:String, title:String, owner:String, owner_name:String, date_upload:String, date_taken:String, count_views:Int, count_faves:Int, count_comments:Int, tags:String, machine_tags:String, urls:PhotoUrls)
{

  lazy val tagsList = {parseTags(tags)}

  lazy val machineTagsList = {parseTags(machine_tags)}

  lazy val allTagsList = {(tagsList ++ machineTagsList).distinct}

  private def parseTags(t:String) = {t.split(" ").filter(_ != "")}

}

case class Favourite(photo: PhotoExcerpt, faved_by: String, date_faved:String)

case class CollectionInfo(page:Int, pages:Int, perPage: Int, total: Int)

case class FavouritesColectionInfo()

// https://www.flickr.com/services/api/misc.urls.html
case class PhotoUrls(largeSquareThumb:String = "", largeThumb:String = "", small:String = "", large:String = "")
{
  def toMap = Map("large_square_thumb" -> largeSquareThumb, "large_thumb" -> largeThumb, "small" -> small, "large" -> large)
}

object PhotoUrls
{

  def apply(m:Map[String, String]):PhotoUrls = PhotoUrls(
    largeSquareThumb = m.getOrElse("large_square_thumb", ""),
    largeThumb = m.getOrElse("large_thumb", ""),
    small = m.getOrElse("small", ""),
    large = m.getOrElse("large", "")
  )

}

case class Dashboard(nsid: String, id:UUID, created_at: DateTime)