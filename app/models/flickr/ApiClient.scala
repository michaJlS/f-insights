package models.flickr

import play.api.libs.json.{JsValue, Json}
import play.api.libs.oauth.{OAuthCalculator, RequestToken, ConsumerKey}
import play.api.libs.ws.{WSResponse, WSRequest, WSClient}
import play.api.Logger // TODO removeme

import scala.concurrent.{Future, ExecutionContext}

/**
 * First version. May require refactoring to a few more classes
 */
class ApiClient (url: String, apiClient: WSClient, consumerKey: ConsumerKey, requestToken: RequestToken)
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

  private def doRequest(prepareFunc: WSRequest => WSRequest)(implicit executor:ExecutionContext):Future[WSResponse] = {
    sign(prepareFunc(getRequest)).get
  }

  private def log(r:WSResponse): WSResponse = {
    Logger.info(r.body)
    r
  }

  private def log(j:JsValue):JsValue = {
    Logger.info(Json.prettyPrint(j))
    j
  }

  private def setApiMethod(method:String)(request:WSRequest):WSRequest = {
    request.withQueryString("method" -> method)
  }

  private def setQueryParams(params:Map[String, String])(request:WSRequest):WSRequest = {
    if (params.isEmpty) {
      request
    } else {
      setQueryParams(params.tail)(request.withQueryString(params.head._1 -> params.head._2))
    }
  }

  private def setOptionalParams(params:Map[String, Option[String]])(request:WSRequest):WSRequest = {
    if (params.isEmpty) {
      request
    } else {
      params.head._2 match {
        case Some(v) => setOptionalParams(params.tail)(request.withQueryString(params.head._1 -> v))
        case None => setOptionalParams(params.tail)(request)
      }
    }
  }

  private def setHttpVerb(verb:String)(request:WSRequest):WSRequest = {
    request.withMethod(verb)
  }

  private def getJson(response:WSResponse)(implicit executor:ExecutionContext):Option[JsValue] = {
    if (isResponseOk(response)) Some(response.json) else None
  }

  def checkToken()(implicit executor:ExecutionContext):Future[Option[JsValue]] = {
    val response = doRequest(setApiMethod("flickr.auth.oauth.checkToken"))
    response.map(getJson)
  }

  def getUserInfo(nsid:String)(implicit executor:ExecutionContext):Future[Option[JsValue]] = {
    val h = setQueryParams(Map("user_id" -> nsid)) _ compose setApiMethod("flickr.people.getInfo")
    doRequest(h).map(getJson)
  }

  /**
   *
   * @param nsid
   * @param page
   * @param perpage max 500
   * @param favedBefore
   * @param favedAfter
   * @return
   */
  def getUserPublicFavorites(nsid:String, page:Int=1, perpage:Int=500,
                             favedBefore:Option[String] = None,
                             favedAfter:Option[String] = None)(implicit executor:ExecutionContext):Future[Option[JsValue]] = {
    val qp = Map("user_id" -> nsid, "page" -> page.toString, "per_page" -> perpage.toString,
                  "extras" -> "date_upload,date_taken,tags,machine_tags,views,media,count_faves,count_comments,url_q,url_m,url_z,url_l")
    val optional = Map("min_fave_date" -> favedAfter, "max_fave_date" -> favedBefore)
    val h = setQueryParams(qp) _ compose setApiMethod("flickr.favorites.getPublicList") _ compose setOptionalParams(optional)
    doRequest(h).map(getJson)
  }

}
