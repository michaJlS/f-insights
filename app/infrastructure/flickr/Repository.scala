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
                                           (implicit executor:ExecutionContext): Future[Option[Seq[Favourite]]] =
    loadSequentialy((page: Int) => getUserPublicFavorites(nsid, token, page, 500, favedBefore, favedAfter))

  def getAllUserPublicFavorites(nsid: String, token: UserToken)(implicit ec: ExecutionContext): Future[Option[Seq[Favourite]]] =
    loadParallely((page: Int) => getUserPublicFavorites(nsid, token, page, 500))

  def getAllUserPublicContacts(nsid: String, token: UserToken)(implicit executor: ExecutionContext): Future[Option[Seq[Contact]]] =
    loadParallely((page: Int) => getUserPublicContacts(nsid, token, page, 1000))

  def getAllUserPhotos(nsid: String, token: UserToken)(implicit executor: ExecutionContext): Future[Option[Seq[PhotoExcerpt]]] =
    loadParallely((page: Int) => getUserPhotos(nsid, token, page, 500))


  def getPhotoFavs(photoId: String, owner: String, token: UserToken, page: Int = 1, perpage: Int = 50)
                  (implicit ec: ExecutionContext): Future[Option[(CollectionInfo, Seq[PhotoFavourite])]]

  def getAllPhotoFavs(photoId: String, owner: String, token: UserToken)
                     (implicit ec: ExecutionContext): Future[Option[Seq[PhotoFavourite]]] =
    loadSequentialy((page: Int) => getPhotoFavs(photoId, owner, token, page, 50))

  protected def loadParallely[T](f: (Int => Future[Option[(CollectionInfo, Seq[T])]]))
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

  private def loadSequentialy[T](f: (Int => Future[Option[(CollectionInfo, Seq[T])]]))
                                (implicit ec: ExecutionContext): Future[Option[Seq[T]]] = {
    def load(page: Int, all: Seq[T] = Seq.empty): OptionT[Future, Seq[T]] = {
      OptionT(f(page))
        .flatMap { case (info, c) =>
          val newAll = all ++ c
          if (page >= info.pages)
            OptionT.apply[Future, Seq[T]](Future.successful(Some(newAll)))
          else
            load(page + 1, newAll)
        }

    }

    load(1).run
  }

}
