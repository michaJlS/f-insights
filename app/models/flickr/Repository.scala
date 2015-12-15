package models.flickr

import scala.concurrent.{ExecutionContext, Future}

trait Repository
{

  def getUserInfo(nsid:String)(implicit executor:ExecutionContext):Future[Option[UserInfo]]

  def getUserPublicFavorites(nsid:String, page:Int=1, perpage:Int=500,
                             favedBefore:Option[String] = None,
                             favedAfter:Option[String] = None)(implicit executor:ExecutionContext):Future[Option[(CollectionInfo, Seq[PhotoExcerpt])]]


  def getAllUserPublicFavoritesSequentially(nsid:String, favedBefore:Option[String] = None, favedAfter:Option[String] = None)(implicit executor:ExecutionContext):Future[Option[Seq[PhotoExcerpt]]] = {
    def load(page:Int, all:Seq[PhotoExcerpt] = Seq[PhotoExcerpt]()):Future[Option[Seq[PhotoExcerpt]]] = {
      getUserPublicFavorites(nsid, page, 500, favedBefore, favedAfter).flatMap(
        _ match  {
          case None => Future {None}
          case Some((info, c)) =>
            if (page >= info.pages) {
              Future {Some(all ++ c)}
            } else {
              load(page + 1, all ++ c)
            }
        }
      )
    }
    load(1)
  }

  def getAllUserPublicFavoritesParallely(nsid:String, favedBefore:Option[String] = None, favedAfter:Option[String] = None)(implicit executor:ExecutionContext):Future[Option[Seq[PhotoExcerpt]]] = {
    getUserPublicFavorites(nsid, 1, 500, favedBefore, favedAfter) flatMap { res =>
      res match {
        case Some((info, photos)) => {

          val ps = for {
            i <- Range(2, info.pages + 1)
          } yield getUserPublicFavorites(nsid, i, 500, favedBefore, favedAfter).map(_.map(_._2))

          Future
            .sequence((ps :+ Future {Some(photos)}).toSeq)
            .map(s => if (s.contains(None)) None else Some(s))
            .map(_ match {
              case Some(s) => Some(s.map(_.get).flatten)
              case _ => None
            })
        }
        case _ => Future {None}
      }
    }
  }

}
