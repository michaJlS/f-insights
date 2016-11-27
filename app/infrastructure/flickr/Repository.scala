package infrastructure.flickr

import domain.entities._

import scala.concurrent.{ExecutionContext, Future}
import scalaz._
import Scalaz._

trait Repository
{

  import Repository.ResultInfo

  def getUserInfo(nsid: String, token: UserToken)(implicit executor: ExecutionContext): Future[Option[UserInfo]]

  def getUserPublicFavorites(nsid:String, token:UserToken, page:Int=1, perpage:Int=500,
                             favedBefore: Option[String] = None, favedAfter: Option[String] = None)
                            (implicit executor:ExecutionContext): Future[Option[(CollectionInfo, Seq[Favourite])]]

  def getUserPublicContacts(nsid: String, token:UserToken, page: Int = 1, perpage: Int = 1000)
                           (implicit executor: ExecutionContext): Future[Option[(CollectionInfo, Seq[Contact])]]

  def getUserPhotos(nsid: String, token: UserToken, page: Int = 1, perpage: Int = 500)
                   (implicit ec: ExecutionContext): Future[Option[(CollectionInfo, Seq[PhotoExcerpt])]]

  def getAllUserPublicFavoritesSequentially(nsid: String, token: UserToken, favedBefore: Option[String] = None, favedAfter: Option[String] = None)
                                           (implicit executor:ExecutionContext): Future[Option[Seq[Favourite]]] =
    loadSequentialyOnlyItems((page: Int) => getUserPublicFavorites(nsid, token, page, 500, favedBefore, favedAfter))

  def getAllUserPublicFavorites(nsid: String, token: UserToken)(implicit ec: ExecutionContext): Future[Option[Seq[Favourite]]] =
    loadParallely((page: Int) => getUserPublicFavorites(nsid, token, page, 500))

  def getAllUserPublicContacts(nsid: String, token: UserToken)(implicit executor: ExecutionContext): Future[Option[Seq[Contact]]] =
    loadParallely((page: Int) => getUserPublicContacts(nsid, token, page, 1000))

  def getAllUserPublicContactsSequentially(nsid: String, token: UserToken)(implicit executor: ExecutionContext): Future[Option[Seq[Contact]]] =
    loadSequentialyOnlyItems((page: Int) => getUserPublicContacts(nsid, token, page, 1000))


  def getUserPublicContactsSequentially(nsid: String, token: UserToken, limitPages: Int)(implicit executor: ExecutionContext): Future[Option[ResultInfo[Contact]]] =
    loadSequentialy((page: Int) => getUserPublicContacts(nsid, token, page, 1000), Some(limitPages))

  def getAllUserPhotos(nsid: String, token: UserToken)(implicit executor: ExecutionContext): Future[Option[Seq[PhotoExcerpt]]] =
    loadParallely((page: Int) => getUserPhotos(nsid, token, page, 500))

  def getAllUserPhotosSequentially(nsid: String, token: UserToken)(implicit executor: ExecutionContext): Future[Option[Seq[PhotoExcerpt]]] =
    loadSequentialyOnlyItems((page: Int) => getUserPhotos(nsid, token, page, 500))

  def getUserPhotosSequentially(nsid: String, token: UserToken, limitPages: Int)(implicit executor: ExecutionContext): Future[Option[ResultInfo[PhotoExcerpt]]] =
    loadSequentialy((page: Int) => getUserPhotos(nsid, token, page, 500))

  def getPhotoFavs(photoId: String, owner: String, token: UserToken, page: Int = 1, perpage: Int = 50)
                  (implicit ec: ExecutionContext): Future[Option[(CollectionInfo, Seq[PhotoFavourite])]]

  def getAllPhotoFavs(photoId: String, owner: String, token: UserToken)
                     (implicit ec: ExecutionContext): Future[Option[Seq[PhotoFavourite]]] =
    loadSequentialyOnlyItems((page: Int) => getPhotoFavs(photoId, owner, token, page, 50))

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

  private def loadSequentialyOnlyItems[T](f: (Int => Future[Option[(CollectionInfo, Seq[T])]]), pagesLimit: Option[Int] = None)
                                         (implicit ec: ExecutionContext): Future[Option[Seq[T]]] =
    OptionT(loadSequentialy(f, pagesLimit)).map(_.items).run

  private def loadSequentialy[T](f: (Int => Future[Option[(CollectionInfo, Seq[T])]]), pagesLimit: Option[Int] = None)
                                (implicit ec: ExecutionContext): Future[Option[ResultInfo[T]]] = {
    def load(page: Int, all: Seq[T] = Seq.empty): OptionT[Future, ResultInfo[T]] = {
      OptionT(f(page))
        .flatMap { case (info, c) =>
          val newAll = all ++ c
          if (page >= maxPages(info.pages, pagesLimit))
            OptionT.apply[Future, ResultInfo[T]](Future.successful(Some(ResultInfo(
              newAll,
              info.total,
              pagesLimit
            ))))
          else
            load(page + 1, newAll)
        }

    }

    load(1).run
  }

  private def maxPages(p: Int, l: Option[Int]) = Math.min(p, l.getOrElse(p))

}

object Repository {

  case class ResultInfo[T](items: Seq[T], total: Int, pagesLimit: Option[Int] = None)

}