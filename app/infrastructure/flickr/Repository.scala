package infrastructure.flickr

import domain.entities._

import scala.concurrent.{ExecutionContext, Future}

trait Repository
{

  def getUserInfo(nsid:String, token:UserToken)(implicit executor:ExecutionContext):Future[Option[UserInfo]]

  def getUserPublicFavorites(nsid:String, token:UserToken, page:Int=1, perpage:Int=500,
                             favedBefore:Option[String] = None,
                             favedAfter:Option[String] = None)(implicit executor:ExecutionContext)
        :Future[Option[(CollectionInfo, Seq[Favourite])]]

  def getUserPublicContacts(nsid: String, token:UserToken, page: Int = 1, perpage: Int = 1000)(implicit executor:ExecutionContext)
        : Future[Option[(CollectionInfo, Seq[Contact])]]

  def getAllUserPublicFavoritesSequentially(nsid:String, token:UserToken, favedBefore:Option[String] = None, favedAfter:Option[String] = None)(implicit executor:ExecutionContext):Future[Option[Seq[Favourite]]] = {
    def load(page:Int, all:Seq[Favourite] = Seq[Favourite]()):Future[Option[Seq[Favourite]]] = {
      getUserPublicFavorites(nsid, token, page, 500, favedBefore, favedAfter).flatMap(
        _ match  {
          case None => Future.successful {None}
          case Some((info, c)) =>
            if (page >= info.pages) {
              Future.successful {Some(all ++ c)}
            } else {
              load(page + 1, all ++ c)
            }
        }
      )
    }
    load(1)
  }

  def getAllUserPublicFavoritesParallely(nsid:String, token:UserToken, favedBefore:Option[String] = None, favedAfter:Option[String] = None)(implicit executor:ExecutionContext)
        :Future[Option[Seq[Favourite]]] = {

    getUserPublicFavorites(nsid, token, 1, 500, favedBefore, favedAfter).
      flatMap {
        case Some((info, photos)) => {

          val ps = for {
            i <- Range(2, info.pages + 1)
          } yield getUserPublicFavorites(nsid, token, i, 500, favedBefore, favedAfter).map(_.map(_._2))

          Future
            .sequence((ps :+ Future {Some(photos)}).toSeq)
            .map(s => if (s.contains(None)) None else Some(s))
            .map(_ match {
              case Some(s) => Some(s.map(_.get).flatten)
              case _ => None
            })
        }
        case _ => Future.successful {None}
      }

  }

  def getAllUserPublicContacts(nsid:String, token:UserToken)(implicit executor:ExecutionContext):Future[Option[Seq[Contact]]] = {

    getUserPublicContacts(nsid, token, 1, 1000).
      flatMap {
        case Some((info, contacts)) => {

          val ps = for {
            i <- Range(2, info.pages + 1)
          } yield getUserPublicContacts(nsid, token, i, 1000).map(_.map(_._2))

          Future
            .sequence((ps :+ Future {Some(contacts)}).toSeq)
            .map(s => if (s.contains(None)) None else Some(s))
            .map(_ match {
              case Some(s) => Some(s.map(_.get).flatten)
              case _ => None
            })
        }
        case _ => Future.successful {None}
    }
  }

}
