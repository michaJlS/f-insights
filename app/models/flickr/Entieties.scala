package models.flickr

/**
 *
 */

case class UserToken(token:String, secret:String)

case class TokenInfo(nsid: String, username: String, fullname: String)

case class UserInfo(id:String, nsid:String, username: String, fullname: String, photosurl: String, uploads:Int, firstupload:String)

case class PhotoExcerpt(id:String, title:String, owner:String, date_upload:String, date_taken:String, count_views:Int, count_faves:Int, count_comments:Int, tags:String, machine_tags:String, urls:PhotoUrls)
{

  lazy val tagsList = {parseTags(tags)}

  lazy val machineTagsList = {parseTags(machine_tags)}

  lazy val allTagsList = {(tagsList ++ machineTagsList).distinct}

  private def parseTags(t:String) = {t.split(" ").filter(_ != "")}

}

case class CollectionInfo(page:Int, pages:Int, perPage: Int, total: Int)

// https://www.flickr.com/services/api/misc.urls.html
case class PhotoUrls(largeSquareThumb:String = "", largeThumb:String = "", small:String = "", large:String = "")

