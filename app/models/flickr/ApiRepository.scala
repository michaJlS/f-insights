package models.flickr

import scala.concurrent.{Future, ExecutionContext}

class ApiRepository(apiClient:ApiClient, parser:ResponseParser) extends Repository
{

  /**
    * This is not a part of the interface
    */
  def checkToken()(implicit executor:ExecutionContext):Future[Option[TokenInfo]] = {
    apiClient.checkToken.map(_.flatMap(parser.getTokenInfo))
  }

  def getUserInfo(nsid: String)(implicit executor:ExecutionContext): Future[Option[UserInfo]] = {
    apiClient.getUserInfo(nsid).map(_.flatMap(parser.getUserInfo))
  }

  def getUserPublicFavorites(nsid: String, page: Int, perpage: Int, favedBefore: Option[String], favedAfter: Option[String])(implicit executor:ExecutionContext)
      : Future[Option[(CollectionInfo, Seq[PhotoExcerpt])]] = {
    apiClient.getUserPublicFavorites(nsid, page, perpage, favedBefore, favedAfter).map(_.flatMap(json =>
      (parser.getPhotosCollectionInfo(json), parser.getPhotos(json)) match {
        case (Some(info), Some(photos)) => Some((info, photos))
        case (_, _) => None
      }
    ))
  }


}
