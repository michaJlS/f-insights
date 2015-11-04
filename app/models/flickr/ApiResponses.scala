package models.flickr

/**
 *
 */
case class TokenInfo(nsid: String, username: String, fullname: String)

case class UserInfo(id:String, nsid:String, username: String, fullname: String, photosurl: String, uploads:Int, firstupload:String)

case class PhotoExcerpt(id:String, title:String, owner:String, date_upload:String, date_taken:String /* ... */ )

case class CollectionInfo(page:Int, pages:Int, perPage: Int, total: Int)


// tags, machine_tags views urls ??