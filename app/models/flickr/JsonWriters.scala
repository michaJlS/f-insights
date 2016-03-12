package models.flickr

import play.api.libs.json._
import play.api.libs.functional.syntax._

/**
  *
  */
object JsonWriters
{

  lazy val userInfo = {
    new Writes[UserInfo] {
      def writes(ui:UserInfo) = {
        Json.obj(
          "id" -> ui.id,
          "nsid" -> ui.nsid,
          "username" -> ui.username,
          "fullname" -> ui.fullname,
          "photosurl" -> ui.photosurl,
          "uploads" -> ui.uploads,
          "firstupload" -> ui.firstupload
        )
      }
    }
  }

  lazy val dashboard = {
    new Writes[Dashboard] {
      def writes(d:Dashboard) = {
        Json.obj(
          "id" -> d.id.toString,
          "nsid" -> d.nsid,
          "created_at" -> d.created_at
        )
      }
    }
  }

}
