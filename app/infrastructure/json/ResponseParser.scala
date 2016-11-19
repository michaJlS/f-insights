package infrastructure.json

import domain.entities._
import play.api.libs.json.{JsArray, JsLookupResult, JsValue}

import scala.util.{Failure, Success, Try}


class ResponseParser {

  // TODO go reads...

  // sometimes flickr returns numbers as just number, and sometimes it wraps numbers with "", what is interpreted as string by the play json lib
  private def asOptStringToInt(j: JsLookupResult): Option[Int] = j.asOpt[String].flatMap(s => Try(s.toInt).toOption)

  private def asOptIntToString(j: JsLookupResult): Option[String] = j.asOpt[Int].flatMap(i => Try(i.toString).toOption)

  private def asOptInt(j: JsLookupResult): Option[Int] = j.asOpt[Int].orElse(asOptStringToInt(j))

  private def asOptString(j: JsLookupResult): Option[String] =
    j.asOpt[String].orElse(asOptIntToString(j))

  def getTokenInfo(json:JsValue):Option[TokenInfo] =
    for {
      user <- (json \ "oauth" \ "user").toOption
      nsid <- (user \ "nsid").asOpt[String]
      username <- (user \ "username").asOpt[String]
      fullname <- (user \ "fullname").asOpt[String]
    } yield TokenInfo(nsid, username, fullname)

  def getUserInfo(json:JsValue):Option[UserInfo] =
    for {
      person <- (json \ "person").toOption
      id <- (person \ "id").asOpt[String]
      nsid <- (person \ "nsid").asOpt[String]
      username <- (person \ "username" \ "_content").asOpt[String]
      fullname <- (person \ "realname" \ "_content").asOpt[String]
      photosurl <- (person \ "photosurl" \ "_content").asOpt[String]
      photos <- (person \ "photos").toOption
      uploads <- asOptInt(photos \ "count" \ "_content")
      firstupload <- (photos \ "firstdate" \ "_content").asOpt[String]

    } yield UserInfo(id, nsid, username, fullname, photosurl, uploads, firstupload)

  def getPhotoFavourite(json: JsValue, photoId: String, owner: String): Option[PhotoFavourite] =
    for {
      favedBy <- (json \ "nsid").asOpt[String]
      username <- (json \ "username").asOpt[String]
      realname <- (json \ "realname").asOpt[String]
      date_faved <- (json \ "favedate").asOpt[String]
    } yield PhotoFavourite(photoId, owner, favedBy, username, realname, date_faved)

  def getPhotoFavourites(json: JsValue, owner: String): Option[Seq[PhotoFavourite]] =
    for {
      photo <- (json \ "photo").toOption
      photoId <- (photo \ "id" ).asOpt[String]
      people <- (photo \ "person" ).toOption.collect({
        case JsArray(x) => x
      })
    } yield people.map(js => getPhotoFavourite(js, photoId, owner)).collect({ case Some(x) => x})

  def getCollectionInfo(json:JsValue, root:String) =
    for {
      ci <- (json \ root).toOption
      page <-  asOptInt(ci \ "page")
      pages <- asOptInt(ci \ "pages")
      perpage <- asOptInt(ci \ "perpage")
      total <- asOptInt(ci \ "total")
    } yield CollectionInfo(page, pages, perpage, total)

  def getPhotosCollectionInfo(json:JsValue):Option[CollectionInfo] = getCollectionInfo(json, "photos")

  def getContactsCollectionInfo(json:JsValue):Option[CollectionInfo] = getCollectionInfo(json, "contacts")

  def getPhotoFavouritesCollectionInfo(json: JsValue) = getCollectionInfo(json, "photo")

  // // https://www.flickr.com/services/api/misc.urls.html
  def getPhotoExcerpt(json:JsValue):Option[PhotoExcerpt] =
    for {
      id <- asOptString(json \ "id")
      title  <- (json \ "title").asOpt[String]
      owner <- (json \ "owner").asOpt[String]
      owner_name <- (json \ "ownername").asOpt[String]
      date_upload <- asOptString((json \ "dateupload"))
      date_taken <- (json \ "datetaken").asOpt[String].orElse(Option(""))
      count_views <- asOptInt(json \ "views").orElse(Option(0))
      count_faves <- asOptInt(json \ "count_faves").orElse(Option(0))
      count_comments <- asOptInt(json \ "count_comments").orElse(Option(0))
      tags <- (json \ "tags").asOpt[String].orElse(Option(""))
      machine_tags <- (json \ "machine_tags").asOpt[String].orElse(Option(""))
      media <-  asOptString(json \ "media")
      url_q <- (json \ "url_q").asOpt[String].orElse(Option(""))
      url_m <- (json \ "url_m").asOpt[String].orElse(Option(""))
      url_z <- (json \ "url_m").asOpt[String].orElse(Option(""))
      url_l <- (json \ "url_m").asOpt[String].orElse(Option(""))
      if media == PhotoExcerpt.medtiaTypePhoto
    } yield PhotoExcerpt(id, title, owner, owner_name, date_upload, date_taken, count_views, count_faves, count_comments, tags, machine_tags,
              PhotoUrls(largeSquareThumb = url_q, largeThumb = url_m, small = url_z, large = url_l))

  def getFavourite(json:JsValue, favFor:String = ""):Option[Favourite] =
    for {
      photo <- getPhotoExcerpt(json)
      date_faved <- (json \ "date_faved").asOpt[String]
    } yield Favourite(photo, favFor, date_faved)

  def getFavourites(json:JsValue, favsFor:String  = ""):Option[Seq[Favourite]] =
    (json \ "photos" \ "photo").toOption match {
      case Some(JsArray(s)) => Some(for {
        js <- s
        fav <- getFavourite(js, favsFor)
      } yield fav)
      case _ => None
    }

  def getContact(json:JsValue, contactOf:String = "") =
    for {
      nsid <- asOptString(json \ "nsid")
      username <- asOptString(json \ "username")
    } yield Contact(nsid, username, contactOf)

  def getContacts(json: JsValue, contactsOf: String = "") =
    (json \ "contacts" \ "contact").toOption.collect {
      case JsArray(s) => for {
        js <- s
        c <- getContact(js, contactsOf)
      } yield c
    }

  def getPhotos(json:JsValue):Option[Seq[PhotoExcerpt]] =
    (json \ "photos" \ "photo").toOption.collect {
      case JsArray(s) => for {
        js <- s
        photo <- getPhotoExcerpt(js)
      } yield photo
    }

  def getFavouritesWithCollectionInfo(json: JsValue, favFor: String = ""): Option[(CollectionInfo, Seq[Favourite])] =
    for {
      info <- getPhotosCollectionInfo(json)
      favs <- getFavourites(json, favFor)
    } yield (info, favs)

  def getContactsWithCollectionInfo(json: JsValue, contactsOf: String = ""): Option[(CollectionInfo, Seq[Contact])] =
    for {
      info <- getContactsCollectionInfo(json)
      contacts <- getContacts(json, contactsOf)
    } yield (info, contacts)

  def getPhotosWithCollectionInfo(json: JsValue): Option[(CollectionInfo, Seq[PhotoExcerpt])] =
    for {
      info <- getPhotosCollectionInfo(json)
      photos <- getPhotos(json)
    } yield (info, photos)

  def getPhotoFavouritesWithCollectionInfo(json: JsValue, owner: String): Option[(CollectionInfo, Seq[PhotoFavourite])] =
    for {
      info <- getPhotoFavouritesCollectionInfo(json)
      photos <- getPhotoFavourites(json, owner)
    } yield (info, photos)

}
