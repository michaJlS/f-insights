package infrastructure.cassandra

import java.util.UUID

import com.websudos.phantom.dsl.{Database, KeySpaceDef}
import domain.entities._
import domain.service.AppRepository
import infrastructure.cassandra.table._

import scala.concurrent.{ExecutionContext, Future}

class FlickrAssistantDb(val keyspace:KeySpaceDef) extends Database(keyspace) with AppRepository
{

  object Dashboards extends ConcreteDashboards with keyspace.Connector
  object Favourites extends ConcreteFavourites with keyspace.Connector
  object AppUserDetails extends ConcreteAppUserDetails with keyspace.Connector
  object Contacts extends ConcreteContacts with keyspace.Connector
  object Photos extends ConcretePhotos with keyspace.Connector

  override def insertFavourite(dashboard_id: UUID, fav: Favourite)(implicit executor:ExecutionContext): Future[Boolean] = Favourites.insertFavourite(dashboard_id, fav)

  override def getFavouritesByDashboardId(dashboard_id: UUID)(implicit executor:ExecutionContext): Future[List[Favourite]] = Favourites.getByDashboardId(dashboard_id)

  override def insertUserDetail(detail: AppUserDetail)(implicit executor:ExecutionContext): Future[Boolean] = AppUserDetails.insertDetail(detail)

  override def getDashboard(nsid: String, dashboard_id: UUID)(implicit executor:ExecutionContext): Future[Option[Dashboard]] = Dashboards.getById(nsid, dashboard_id)

  override def getUserDetail(nsid: String, key: String)(implicit executor:ExecutionContext): Future[Option[AppUserDetail]] = AppUserDetails.getDetail(nsid, key)

  override def insertDashboard(dashboard: Dashboard)(implicit executor:ExecutionContext): Future[Boolean] = Dashboards.insertDashboard(dashboard)

  override def insertContact(dashboardId: UUID, contact: Contact)(implicit executor: ExecutionContext): Future[Boolean] = Contacts.insertContact(dashboardId, contact)

  override def getContactsByDashboardId(dashboardId: UUID)(implicit executor: ExecutionContext): Future[List[Contact]] = Contacts.getByDashboardId(dashboardId)

  override def insertUserPhoto(dashboardId:UUID, photo: PhotoExcerpt)(implicit executor:ExecutionContext): Future[Boolean] = Photos.insertPhoto(dashboardId, photo)

  override def getPhotosByDashboardId(dashboardId: UUID)(implicit executor:ExecutionContext): Future[List[PhotoExcerpt]] = Photos.getByDashboardId(dashboardId)

}
