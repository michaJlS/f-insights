package infrastructure.json

import domain.entities._
import play.api.libs.json._

/**
  *
  */
object JsonWrites
{

  implicit val dashboardWrites = new Writes[Dashboard] {
    def writes(d:Dashboard) =  Json.obj(
      "id" -> d.id.toString,
      "nsid" -> d.nsid,
      "created_at" -> d.created_at
    )
  }

  implicit val userInfoWrites = new Writes[UserInfo] {
      def writes(ui:UserInfo) =  Json.obj(
          "id" -> ui.id,
          "nsid" -> ui.nsid,
          "username" -> ui.username,
          "fullname" -> ui.fullname,
          "photosurl" -> ui.photosurl,
          "uploads" -> ui.uploads,
          "firstupload" -> ui.firstupload
        )
    }

  implicit val photoUrlsWrites = new Writes[PhotoUrls] {
      def writes(u:PhotoUrls) =  Json.obj(
          "largeSquareThumb" -> u.largeSquareThumb,
          "largeThumb" -> u.largeThumb,
          "small" -> u.small,
          "large" -> u.large
        )
    }

  implicit val photoExcerptWrites = new Writes[PhotoExcerpt] {
      def writes(p:PhotoExcerpt) = Json.obj(
          "id" -> p.id,
          "title" -> p.title,
          "owner" -> p.owner,
          "owner_name" -> p.owner_name,
          "date_upload" -> p.date_upload,
          "date_taken" -> p.date_taken,
          "count_views" -> p.count_views,
          "count_faves" -> p.count_faves,
          "count_comments" -> p.count_comments,
          "urls" -> p.urls
        )
    }
  
  implicit val favouriteWrites = new Writes[Favourite] {
      def writes(f:Favourite) = Json.obj(
          "date_faved" -> f.date_faved,
          "faved_by" -> f.faved_by,
          "photo" -> f.photo
        )
    }

  implicit val favouritesWrites = new Writes[Seq[Favourite]] {
      def writes(c:Seq[Favourite]) =  JsArray(c.map(Json.toJson(_)))
    }

  implicit val favsTagStatWrites =  new Writes[FavTagStats] {
      def writes(tagStats:FavTagStats) = Json.obj(
        "tag" -> tagStats.tag,
        "count" -> tagStats.count,
        "photos" -> tagStats.photos
      )
    }

  implicit val favsTagsStatsWrites = new Writes[Seq[FavTagStats]] {
      def writes(stats:Seq[FavTagStats]) = JsArray(stats.map(Json.toJson(_)))
    }

  implicit val favsOwnerStatsWrites = new Writes[FavOwnerStats] {
      def writes(ownerStats:FavOwnerStats) = Json.obj(
        "owner" -> ownerStats.owner,
        "owner_name" -> ownerStats.owner_name,
        "count" -> ownerStats.count,
        "photos" -> ownerStats.photos
      )
    }

  implicit val favsOwnersStatsWrites =  new Writes[Seq[FavOwnerStats]] {
      def writes(stats: Seq[FavOwnerStats]) = JsArray(stats.map(Json.toJson(_)))
    }

  implicit val userContactWrites = new Writes[Contact] {
      def writes(contact: Contact) = Json.obj(
        "nsid" -> contact.nsid,
        "username" -> contact.username,
        "contactOf" -> contact.contactOf
      )
    }

  implicit val userContactsWrites = new Writes[Seq[Contact]] {
      def writes(contacts: Seq[Contact]) = JsArray(contacts.map(Json.toJson(_)))
    }

  implicit val monthlyStatsWrites = new Writes[MonthlyStats] {
    def writes(stats: MonthlyStats) = Json.obj(
      "month" -> stats.month,
      "uploaded" -> stats.uploaded,
      "faved" -> stats.faved,
      "got_favs" -> stats.gotFavs
    )
  }

  implicit val monthlyStatsSetWrites = new Writes[Seq[MonthlyStats]] {
    def writes(stats: Seq[MonthlyStats]) = JsArray(stats.map(Json.toJson(_)))
  }

  implicit val photoTagStatsWrites = new Writes[PhotoTagStats] {
    def writes(s: PhotoTagStats) = Json.obj(
      "tag" -> s.tag,
      "count" -> s.count,
      "avg_points" -> s.avgPoints,
      "top_avg_points" -> s.topAvgPoints,
      "photos" -> s.photos
    )
  }

  implicit val photosTagStatsWrites = new Writes[Seq[PhotoTagStats]] {
    def writes(stats: Seq[PhotoTagStats]) = JsArray(stats.map(Json.toJson(_)))
  }

  implicit val favingUserStatsWrites = new Writes[FavingUserStats] {
    def writes(s: FavingUserStats) = Json.obj(
      "user" -> s.user,
      "username" -> s.username,
      "realname" -> s.realname,
      "count" -> s.count,
      "frist_fav" -> s.firstFav,
      "last_fav" -> s.lastFav
    )
  }

  implicit val favingUsersStatsWrites = new Writes[Seq[FavingUserStats]] {
    def writes(stats: Seq[FavingUserStats]) = JsArray(stats.map(Json.toJson(_)))
  }

}
