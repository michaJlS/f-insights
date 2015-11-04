package models.flickr

import play.api.libs.json.{JsLookupResult, JsValue}
import scala.util.{Try, Success, Failure}

/**
 *
 */
class ResponseParser
{

  private def asOptStringToInt(j: JsLookupResult):Option[Int] = {
    j.asOpt[String].map(s => Try(s.toInt)).flatMap(_ match {
      case Success(i) => Some(i)
      case Failure(_) => None
    })
  }

  // sometimes flickr returns numbers as just number, and sometimes it wraps numbers with "", what is interpreted as string by the play json lib
  private def asOptInt(j: JsLookupResult):Option[Int] = {
    j.asOpt[Int].orElse(asOptStringToInt(j))
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

}
