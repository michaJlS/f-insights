package models.alerf.flickr

import play.api.libs.json.JsValue
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

  private def setApiMethod(method:String)(request:WSRequest):WSRequest = {
    request.withQueryString("method" -> method)
  }

  private def setGetParams(params:Map[String, String])(request:WSRequest):WSRequest = {
    params.head match {
      case Tuple2(key, value) => setGetParams(params.tail)(request.withQueryString(key -> value))
      case _ => request
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
        user <- json.\("oauth").\("user").toOption
        nsid <- user.\("nsid").toOption
        username <- user.\("username").toOption
        fullname <- user.\("fullname").toOption
      } yield TokenInfo(nsid.toString, username.toString, fullname.toString)
    }
    response.filter(isResponseOk).map(_.json).map(parser)
  }

  def getUserInfo(nsid:String):Future[Option[UserInfo]] = {
    val h = setGetParams(Map("user_id" -> nsid)) _ compose setApiMethod("flickr.people.getInfo")
    val response = doRequest(h)
    def parser(json:JsValue):Option[UserInfo] = {
      None
    }
    response.filter(isResponseOk).map(_.json).map(_ => None)
  }

}
