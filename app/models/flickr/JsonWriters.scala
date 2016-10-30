package models.flickr

import play.api.libs.json._
import play.api.libs.functional.syntax._

/**
  *
  */
object JsonWriters
{

  implicit lazy val userInfo = new Writes[UserInfo] {
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

  implicit lazy val dashboard =  new Writes[Dashboard] {
      def writes(d:Dashboard) = {
        Json.obj(
          "id" -> d.id.toString,
          "nsid" -> d.nsid,
          "created_at" -> d.created_at
        )
      }
    }

  implicit lazy val photoUrls = new Writes[PhotoUrls] {
      def writes(u:PhotoUrls) = {
        Json.obj(
          "largeSquareThumb" -> u.largeSquareThumb,
          "largeThumb" -> u.largeThumb,
          "small" -> u.small,
          "large" -> u.large
        )
      }
    }

  // case class PhotoExcerpt(id:String, title:String, owner:String, owner_name:String, date_upload:String, date_taken:String, count_views:Int, count_faves:Int, count_comments:Int, tags:String, machine_tags:String, urls:PhotoUrls)

  implicit lazy val photoExcerpt = new Writes[PhotoExcerpt] {
      def writes(p:PhotoExcerpt) = {
        Json.obj(
          "id" -> p.id,
          "title" -> p.title,
          "owner" -> p.owner,
          "owner_name" -> p.owner_name,
          "date_upload" -> p.date_upload,
          "date_taken" -> p.date_taken,
          "count_views" -> p.count_views,
          "count_faves" -> p.count_faves,
          "count_comments" -> p.count_comments,
          "urls" -> photoUrls.writes(p.urls)
        )
      }
    }
  
  implicit lazy val favourite = new Writes[Favourite] {
      def writes(f:Favourite) = {
        Json.obj(
          "date_faved" -> f.date_faved,
          "faved_by" -> f.faved_by,
          "photo" -> photoExcerpt.writes(f.photo)
        )
      }
    }

  implicit lazy val favourites = new Writes[Seq[Favourite]] {
      def writes(c:Seq[Favourite]) = {
        JsArray(c.map(favourite.writes(_)))
      }
    }

  implicit lazy val favsTagStat =  new Writes[FavTagStats] {
      def writes(tagStats:FavTagStats) = Json.obj(
        "tag" -> tagStats.tag,
        "count" -> tagStats.count,
        "photos" -> favourites.writes(tagStats.photos)
      )
    }

  implicit lazy val favsTagsStats = new Writes[Seq[FavTagStats]] {
      def writes(stats:Seq[FavTagStats]) = JsArray(stats.map(favsTagStat.writes(_)).toSeq)
    }

  implicit lazy val favsOwnerStats = new Writes[FavOwnerStats] {
      def writes(ownerStats:FavOwnerStats) = Json.obj(
        "owner" -> ownerStats.owner,
        "owner_name" -> ownerStats.owner_name,
        "count" -> ownerStats.count,
        "photos" -> favourites.writes(ownerStats.photos)
      )
    }

  implicit lazy val favsOwnersStats =  new Writes[Seq[FavOwnerStats]] {
      def writes(stats: Seq[FavOwnerStats]) = JsArray(stats.map(favsOwnerStats.writes(_)).toSeq)
    }

  implicit lazy val userContact = new Writes[Contact] {
      def writes(contact: Contact) = Json.obj(
        "nsid" -> contact.nsid,
        "username" -> contact.username,
        "contactOf" -> contact.contactOf
      )
    }

  implicit lazy val userContacts = new Writes[Seq[Contact]] {
      def writes(contacts: Seq[Contact]) = JsArray(contacts.map(userContact.writes(_)).toSeq)
    }

}
