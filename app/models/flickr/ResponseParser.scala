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

  def getPhotosCollectionInfo(json:JsValue):Option[CollectionInfo] = {
    for {
      photos <- (json \ "photos").toOption
      page <-  asOptInt(photos \ "page")
      pages <- asOptInt(photos \ "pages")
      perpage <- asOptInt(photos \ "perpage")
      total <- asOptInt(photos \ "total")
    } yield CollectionInfo(page, pages, perpage, total)
  }

  def getPhotoExcerpt(json:JsValue):Option[PhotoExcerpt] = {
    // // https://www.flickr.com/services/api/misc.urls.html
    for {
      id <- asOptString(json \ "id")
      title  <- (json \ "title").asOpt[String]
      owner <- (json \ "owner").asOpt[String]
      date_upload <- asOptString((json \ "dateupload"))
      date_taken <- (json \ "datetaken").asOpt[String]
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
    } yield PhotoExcerpt(id, title, owner, date_upload, date_taken, count_views, count_faves, count_comments, tags, machine_tags,
              PhotoUrls(largeSquareThumb = url_q, largeThumb = url_m, small = url_z, large = url_l))
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

}
