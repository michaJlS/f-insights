package models.alerf.flickr

import play.api.libs.json._
import play.api.libs.oauth.{OAuthCalculator, RequestToken, ConsumerKey}
import play.api.libs.ws.{WSResponse, WSRequest, WSClient}
import play.api.Logger


import scala.concurrent.{Future, ExecutionContext}



/**
 * First version. May require refactoring to a few more classes
 */
class ApiClient (url: String, apiClient: WSClient, consumerKey: ConsumerKey, requestToken: RequestToken, implicit val context:ExecutionContext)
{

  private val calculator = OAuthCalculator(consumerKey, requestToken)

  private def getRequest:WSRequest = {
    apiClient
      .url(url)
      .withQueryString("format" -> "json",  "nojsoncallback" -> "1", "api_key" -> consumerKey.key)
  }

  private def sign(request: WSRequest):WSRequest = {
    request.sign(calculator)
  }

  private def isResponseOk(response:WSResponse) = {
    response.status > 199 && response.status < 300
  }

  private def doRequest(prepareFunc: WSRequest => WSRequest):Future[WSResponse] = {
    sign(prepareFunc(getRequest)).get
  }

  private def log(r:WSResponse): WSResponse = {
    Logger.info(r.body)
    r
  }

  private def setApiMethod(method:String)(request:WSRequest):WSRequest = {
    request.withQueryString("method" -> method)
  }

  private def setGetParams(params:Map[String, String])(request:WSRequest):WSRequest = {
    if (params.isEmpty) {
      request
    } else {
      setGetParams(params.tail)(request.withQueryString(params.head._1 -> params.head._2))
    }
  }

  private def setHttpVerb(verb:String)(request:WSRequest):WSRequest = {
    request.withMethod(verb)
  }

  def checkToken:Future[Option[TokenInfo]] = {
    val response = doRequest(setApiMethod("flickr.auth.oauth.checkToken"))
    // move to companion object?
    def parser(json:JsValue):Option[TokenInfo] = {
      for {
        user <- (json \ "oauth" \ "user").toOption
        nsid <- (user \ "nsid").asOpt[String]
        username <- (user \ "username").asOpt[String]
        fullname <- (user \ "fullname").asOpt[String]
      } yield TokenInfo(nsid, username, fullname)
    }
    response.filter(isResponseOk).map(_.json).map(parser)
  }

  def getUserInfo(nsid:String):Future[Option[UserInfo]] = {
    val h = setGetParams(Map("user_id" -> nsid)) _ compose setApiMethod("flickr.people.getInfo")
    val response = doRequest(h)
    def parser(json:JsValue):Option[UserInfo] = {
      for {
        person <- (json \ "person").toOption
        id <- (person \ "id").asOpt[String]
        nsid <- (person \ "nsid").asOpt[String]
        username <- (person \ "username" \ "_content").asOpt[String]
        fullname <- (person \ "realname" \ "_content").asOpt[String]
        photosurl <- (person \ "photosurl" \ "_content").asOpt[String]
        photos <- (person \ "photos").toOption
        uploads <- (photos \ "count" \ "_content").asOpt[Int]
        firstupload <- (photos \ "firstdate" \ "_content").asOpt[String]

      } yield UserInfo(id, nsid, username, fullname, photosurl, uploads, firstupload)
    }
    response.filter(isResponseOk).map(_.json).map(parser)
  }


}
