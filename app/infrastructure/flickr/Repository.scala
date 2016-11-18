package infrastructure.flickr

import domain.entities._

import scala.concurrent.{ExecutionContext, Future}
import scalaz._
import Scalaz._

trait Repository
{

  def getUserInfo(nsid: String, token: UserToken)(implicit executor:ExecutionContext):Future[Option[UserInfo]]

  def getUserPublicFavorites(nsid:String, token:UserToken, page:Int=1, perpage:Int=500,
                             favedBefore: Option[String] = None,
                             favedAfter: Option[String] = None)
                            (implicit executor:ExecutionContext): Future[Option[(CollectionInfo, Seq[Favourite])]]

  def getUserPublicContacts(nsid: String, token:UserToken, page: Int = 1, perpage: Int = 1000)(implicit executor:ExecutionContext)
        : Future[Option[(CollectionInfo, Seq[Contact])]]

  def getAllUserPublicFavoritesSequentially(nsid: String, token: UserToken, favedBefore: Option[String] = None, favedAfter: Option[String] = None)
                                           (implicit executor:ExecutionContext): Future[Option[Seq[Favourite]]] = {
    def load(page:Int, all:Seq[Favourite] = Seq[Favourite]()): OptionT[Future, Seq[Favourite]] =
      OptionT(getUserPublicFavorites(nsid, token, page, 500, favedBefore, favedAfter))
        .flatMap { case (info, c) =>
          if (page >= info.pages)
            OptionT.apply[Future, Seq[Favourite]](Future.successful{Some(all ++ c)})
          else
            load(page + 1, all ++ c)
        }

    load(1).run
  }

  def getAllUserPublicFavoritesParallely(nsid: String, token: UserToken, favedBefore: Option[String] = None, favedAfter: Option[String] = None)
                                        (implicit ec: ExecutionContext): Future[Option[Seq[Favourite]]] =
    OptionT(getUserPublicFavorites(nsid, token, 1, 500, favedBefore, favedAfter))
      .flatMapF { case (info, photos) =>
        val ps = Range(2, info.pages + 1)
          .map(i => getUserPublicFavorites(nsid, token, i, 500, favedBefore, favedAfter).map(_.map(_._2)))
          .toSeq :+ Future.successful {Some(photos)}

        Future.sequence(ps)
      }
      .filter(s => !s.contains(None))
      .map(_.map(_.get).flatten)
      .run

  def getAllUserPublicContacts(nsid:String, token:UserToken)(implicit executor: ExecutionContext): Future[Option[Seq[Contact]]] =
    OptionT(getUserPublicContacts(nsid, token, 1, 1000))
      .flatMapF { case (info, contacts) =>
        val ps = Range(2, info.pages + 1)
          .map(i => getUserPublicContacts(nsid, token, i, 1000).map(_.map(_._2)))
          .toSeq :+ Future.successful{Some(contacts)}

        Future.sequence(ps)
      }
      .filter(s => !s.contains(None))
      .map(_.map(_.get).flatten)
      .run

}
