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
  def checkToken(token:UserToken)(implicit executor:ExecutionContext):Future[Option[TokenInfo]] = {
    apiClient.checkToken(token).map(_.flatMap(parser.getTokenInfo))
  }

  def getUserInfo(nsid: String, token:UserToken)(implicit executor:ExecutionContext): Future[Option[UserInfo]] = {
    apiClient.getUserInfo(nsid, token).map(_.flatMap(parser.getUserInfo))
  }

  def getUserPublicFavorites(nsid: String, token:UserToken, page: Int, perpage: Int, favedBefore: Option[String], favedAfter: Option[String])(implicit executor:ExecutionContext)
      : Future[Option[(CollectionInfo, Seq[Favourite])]] = {
    apiClient.
      getUserPublicFavorites(nsid, token, page, perpage, favedBefore, favedAfter).
      map(_.flatMap(json => parser.getFavouritesWithCollectionInfo(json, nsid) ))
  }

  def getUserPublicContacts(nsid: String, token:UserToken, page: Int, perpage: Int)(implicit executor:ExecutionContext)
      : Future[Option[(CollectionInfo, Seq[Contact])]] = {
    apiClient.
      getUserPublicContacts(nsid, token, page, perpage).
      map(_.flatMap(json => parser.getContactsWithCollectionInfo(json, nsid) ))
  }

}
