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

  lazy val photoUrls = {
    new Writes[PhotoUrls] {
      def writes(u:PhotoUrls) = {
        Json.obj(
          "largeSquareThumb" -> u.largeSquareThumb,
          "largeThumb" -> u.largeThumb,
          "small" -> u.small,
          "large" -> u.large
        )
      }
    }
  }

  // case class PhotoExcerpt(id:String, title:String, owner:String, owner_name:String, date_upload:String, date_taken:String, count_views:Int, count_faves:Int, count_comments:Int, tags:String, machine_tags:String, urls:PhotoUrls)

  lazy val photoExcerpt = {
    new Writes[PhotoExcerpt] {
      def writes(p:PhotoExcerpt) = {
        Json.obj(
          "urls" -> photoUrls.writes(p.urls)
        )
      }
    }
  }

  lazy val favourite = {
    new Writes[Favourite] {
      def writes(f:Favourite) = {
        Json.obj(
          "date_faved" -> f.date_faved,
          "faved_by" -> f.faved_by,
          "photo" -> photoExcerpt.writes(f.photo)
        )
      }
    }
  }

  lazy val favourites = {
    new Writes[Seq[Favourite]] {
      def writes(c:Seq[Favourite]) = {
        JsArray(c.map(favourite.writes(_)))
      }
    }
  }

  lazy val richFavsTagsStats = {
    new Writes[Map[String, (Int, Seq[Favourite])]] {

      private def unwrappedCountAndFavs(tag:String, count:Int, favs: Seq[Favourite]) = {
        Json.obj(
          "tag" -> tag,
          "count" -> count,
          "photos" -> favourites.writes(favs)
        )
      }

      private def unwrappedTag(tag:String, v:(Int, Seq[Favourite])) = unwrappedCountAndFavs(tag, v._1, v._2)

      def writes(stats:Map[String, (Int, Seq[Favourite])]) = JsArray(stats.map(x => unwrappedTag(x._1, x._2)).toSeq)

    }
  }

  lazy val richFavsOwnersStats = {
    new Writes[Map[String, (String, String, Int, Seq[Favourite])]] {
      def writes(stats: Map[String, (String, String, Int, Seq[Favourite])]) = {
        Json.obj("1" -> 1)
      }
    }
  }


}
