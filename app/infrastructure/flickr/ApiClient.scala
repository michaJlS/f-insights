package infrastructure.flickr

import domain.entities.UserToken
import play.api.libs.json.JsValue
import play.api.libs.oauth.{ConsumerKey, OAuthCalculator, RequestToken}
import play.api.libs.ws.{WSClient, WSRequest, WSResponse, WSSignatureCalculator}

import scala.concurrent.{ExecutionContext, Future}

class ApiClient (url: String, apiClient: WSClient, consumerKey: ConsumerKey)
{

  import FlickrWsRequest._
  import FlickrWsResponse._

  def checkToken(implicit token: UserToken, ec: ExecutionContext): Future[Option[JsValue]] =
    doRequest("flickr.auth.oauth.checkToken")

  def getUserInfo(nsid: String)
                 (implicit token: UserToken, ec: ExecutionContext): Future[Option[JsValue]] =
    doRequest("flickr.people.getInfo", Map("user_id" -> nsid))

  def getUserPublicFavorites(nsid: String, page: Int = 1, perpage: Int = 500, favedBefore: Option[String] = None, favedAfter: Option[String] = None)
                            (implicit token: UserToken, ec: ExecutionContext): Future[Option[JsValue]] =
    doRequest(
      "flickr.favorites.getPublicList",
      Map(
        "user_id" -> nsid,
        "page" -> page.toString,
        "per_page" -> perpage.toString,
        "extras" -> "owner_name,date_upload,date_taken,tags,machine_tags,views,media,count_faves,count_comments,url_q,url_m,url_z,url_l"
      ),
      Map("min_fave_date" -> favedAfter, "max_fave_date" -> favedBefore)
    )

  def getUserPublicContacts(nsid: String, page: Int = 1, perpage: Int = 1000)
                           (implicit token: UserToken, ec: ExecutionContext) =
    doRequest("flickr.contacts.getPublicList", Map("user_id" -> nsid, "page" -> page.toString, "per_page" -> perpage.toString))


  def getUserPhotos(nsid: String, page: Int = 1, perpage: Int = 500)
                   (implicit token: UserToken, ec: ExecutionContext): Future[Option[JsValue]] =
    doRequest(
      "flickr.people.getPhotos",
      Map(
        "user_id" -> nsid,
        "safe_search" -> "3",
        "content_type" -> "1",
        "privacy_filter" -> "1",
        "extras" -> "date_upload,date_taken,owner_name,tags,machine_tags,views,media,count_faves,count_comments,url_q,url_m,url_z,url_l",
        "page" -> page.toString,
        "per_page" -> perpage.toString
      )
    )

  private def doRequest(apiMethod: String, params: Map[String, String] = Map.empty, paramsOpt: Map[String, Option[String]] = Map.empty)
                       (implicit token: UserToken, executionContext: ExecutionContext):Future[Option[JsValue]] =
    getRequest()
      .setQueryParams(params)
      .setOptionalQueryParams(paramsOpt)
      .setApiMethod(apiMethod)
      .sign(calc(token))
      .get()
      .map(getJson)

  private def getRequest(): WSRequest =
    apiClient
      .url(url)
      .withQueryString("format" -> "json",  "nojsoncallback" -> "1", "api_key" -> consumerKey.key)

  private def getJson(response: WSResponse)
                     (implicit executor:ExecutionContext): Option[JsValue] =
    if (response.isSuccess()) Some(response.json) else None

  private def calc(token: UserToken) = OAuthCalculator(consumerKey, RequestToken(token.token, token.secret))

}
