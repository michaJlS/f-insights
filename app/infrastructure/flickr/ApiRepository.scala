package infrastructure.flickr

import domain._
import domain.entities._
import infrastructure.json.ResponseParser

import scala.concurrent.{ExecutionContext, Future}

class ApiRepository(apiClient:ApiClient, parser:ResponseParser) extends Repository
{

  /**
    * This is not a part of the interface
    */
  def checkToken(token: UserToken)(implicit ec: ExecutionContext):Future[Option[TokenInfo]] = {
    apiClient.checkToken(token, ec).map(_.flatMap(parser.getTokenInfo))
  }

  def getUserInfo(nsid: String, token: UserToken)(implicit ec: ExecutionContext): Future[Option[UserInfo]] = {
    apiClient.getUserInfo(nsid)(token, ec).map(_.flatMap(parser.getUserInfo))
  }

  def getUserPublicFavorites(nsid: String, token: UserToken, page: Int, perpage: Int, favedBefore: Option[String], favedAfter: Option[String])
                            (implicit ec: ExecutionContext)
      : Future[Option[(CollectionInfo, Seq[Favourite])]] = {
    apiClient.
      getUserPublicFavorites(nsid, page, perpage, favedBefore, favedAfter)(token, ec).
      map(_.flatMap(json => parser.getFavouritesWithCollectionInfo(json, nsid) ))
  }

  def getUserPublicContacts(nsid: String, token:UserToken, page: Int, perpage: Int)(implicit ec:ExecutionContext)
      : Future[Option[(CollectionInfo, Seq[Contact])]] = {
    apiClient.
      getUserPublicContacts(nsid, page, perpage)(token, ec).
      map(_.flatMap(json => parser.getContactsWithCollectionInfo(json, nsid) ))
  }

}
