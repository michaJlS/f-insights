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

  def getUserPublicContacts(nsid: String, token:UserToken, page: Int = 1, perpage: Int = 1000)
                           (implicit executor: ExecutionContext): Future[Option[(CollectionInfo, Seq[Contact])]]

  def getUserPhotos(nsid: String, token: UserToken, page: Int = 1, perpage: Int = 500)
                   (implicit ec: ExecutionContext): Future[Option[(CollectionInfo, Seq[PhotoExcerpt])]]

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

  def getAllUserPublicFavorites(nsid: String, token: UserToken)(implicit ec: ExecutionContext): Future[Option[Seq[Favourite]]] =
    parallelLoader((page: Int) => getUserPublicFavorites(nsid, token, page, 500))

  def getAllUserPublicContacts(nsid: String, token: UserToken)(implicit executor: ExecutionContext): Future[Option[Seq[Contact]]] =
    parallelLoader((page: Int) => getUserPublicContacts(nsid, token, page, 1000))

  def getAllUserPhotos(nsid: String, token: UserToken)(implicit executor: ExecutionContext): Future[Option[Seq[PhotoExcerpt]]] =
    parallelLoader((page: Int) => getUserPhotos(nsid, token, page, 500))

  protected def parallelLoader[T](f: (Int => Future[Option[(CollectionInfo, Seq[T])]]))
                               (implicit ec: ExecutionContext): Future[Option[Seq[T]]] =
    OptionT(f(1))
      .flatMapF { case (info, coll) =>
        val ps = Range(2, info.pages + 1)
          .map(i => f(i).map(_.map(_._2)))
          .toSeq :+ Future.successful{Some(coll)}

        Future.sequence(ps)
      }
      .filter(s => !s.contains(None))
      .map(_.map(_.get).flatten)
      .run

}
