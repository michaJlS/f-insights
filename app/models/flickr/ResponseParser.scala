package models.flickr

import play.api.libs.json.{JsArray, JsLookupResult, JsValue}
import scala.util.{Try, Success, Failure}

/**
 *
 */
class ResponseParser
{

  // TODO move me to better place
  val medtiaTypePhoto = "photo"

  // sometimes flickr returns numbers as just number, and sometimes it wraps numbers with "", what is interpreted as string by the play json lib
  // TODO http://alvinalexander.com/scala/scala-2.10-implicit-class-example
  private def asOptStringToInt(j: JsLookupResult):Option[Int] = {
    j.asOpt[String].map(s => Try(s.toInt)).flatMap(_ match {
      case Success(i) => Some(i)
      case Failure(_) => None
    })
  }

  private def asOptIntToString(j: JsLookupResult):Option[String] = {
    j.asOpt[Int].map(i => Try(i.toString)).flatMap(_ match {
      case Success(s) => Some(s)
      case Failure(_) => None
    })
  }

  private def asOptInt(j: JsLookupResult):Option[Int] = {
    j.asOpt[Int].orElse(asOptStringToInt(j))
  }

  private def asOptString(j: JsLookupResult):Option[String] = {
    j.asOpt[String].orElse(asOptIntToString(j))
  }

  def getTokenInfo(json:JsValue):Option[TokenInfo] = {
    for {
      user <- (json \ "oauth" \ "user").toOption
      nsid <- (user \ "nsid").asOpt[String]
      username <- (user \ "username").asOpt[String]
      fullname <- (user \ "fullname").asOpt[String]
    } yield TokenInfo(nsid, username, fullname)
  }

  def getUserInfo(json:JsValue):Option[UserInfo] = {
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
  }

  def getCollectionInfo(json:JsValue, root:String) = {
    for {
      ci <- (json \ root).toOption
      page <-  asOptInt(ci \ "page")
      pages <- asOptInt(ci \ "pages")
      perpage <- asOptInt(ci \ "perpage")
      total <- asOptInt(ci \ "total")
    } yield CollectionInfo(page, pages, perpage, total)
  }

  def getPhotosCollectionInfo(json:JsValue):Option[CollectionInfo] = getCollectionInfo(json, "photos")

  def getContactsCollectionInfo(json:JsValue):Option[CollectionInfo] = getCollectionInfo(json, "contacts")

  def getPhotoExcerpt(json:JsValue):Option[PhotoExcerpt] = {
    // // https://www.flickr.com/services/api/misc.urls.html
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
      if media == medtiaTypePhoto
    } yield PhotoExcerpt(id, title, owner, owner_name, date_upload, date_taken, count_views, count_faves, count_comments, tags, machine_tags,
              PhotoUrls(largeSquareThumb = url_q, largeThumb = url_m, small = url_z, large = url_l))
  }

  def getFavourite(json:JsValue, favFor:String = ""):Option[Favourite] = {
    for {
      photo <- getPhotoExcerpt(json)
      date_faved <- (json \ "date_faved").asOpt[String]
    } yield Favourite(photo, favFor, date_faved)
  }

  def getFavourites(json:JsValue, favsFor:String  = ""):Option[Seq[Favourite]] = {
    (json \ "photos" \ "photo").toOption match {
      case Some(JsArray(s)) => Some(for {
        js <- s
        fav <- getFavourite(js, favsFor)
      } yield fav)
      case _ => None
    }
  }

  def getContact(json:JsValue, contactOf:String = "") = {
    for {
      nsid <- asOptString(json \ "nsid")
      username <- asOptString(json \ "username")
    } yield Contact(nsid, username, contactOf)
  }

  def getContacts(json:JsValue, contactsOf:String = "") = {
    (json \ "contacts" \ "contact").toOption match {
      case Some(JsArray(s)) => Some(for {
        js <- s
        c <- getContact(js, contactsOf)
      } yield c)
      case _ => None
    }
  }

  def getPhotos(json:JsValue):Option[Seq[PhotoExcerpt]] = {
    (json \ "photos" \ "photo").toOption match {
      case Some(JsArray(s)) => Some(for {
        js <- s
        photo <- getPhotoExcerpt(js)
      } yield photo)
      case _ => None
    }
  }

  def getFavouritesWithCollectionInfo(json:JsValue, favFor:String = "") = {
    (getPhotosCollectionInfo(json), getFavourites(json, favFor)) match {
      case (Some(info), Some(photos)) => Some((info, photos))
      case (_, _) => None
    }
  }

  def getContactsWithCollectionInfo(json:JsValue, contactsOf:String = "") = {
    (getContactsCollectionInfo(json), getContacts(json, contactsOf)) match {
      case (Some(info), Some(contacts)) => Some((info, contacts))
      case (_, _) => None
    }
  }

}
