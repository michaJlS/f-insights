package infrastructure.cassandra.table


import java.util.UUID

import com.websudos.phantom.dsl._
import domain.entities.Contact

import scala.concurrent.Future


class Contacts extends CassandraTable[Contacts, Contact]
{

  override lazy val tableName = "contacts"

  object dashboard_id extends UUIDColumn(this) with PrimaryKey[UUID]
  object contact_of extends StringColumn(this) with ClusteringOrder[String]
  object nsid extends StringColumn(this) with ClusteringOrder[String]
  object username extends StringColumn(this)

  override def fromRow(r: Row) = Contact(nsid(r), username(r), contact_of(r))

}


abstract class ConcreteContacts extends Contacts with RootConnector
{

  def insertContact(dashboardId:UUID, contact:Contact) = {
    insert.
      value(_.dashboard_id, dashboardId).
      value(_.contact_of, contact.contactOf).
      value(_.nsid, contact.nsid).
      value(_.username, contact.username).
      future.
      map(_ => true)
  }

  def getByDashboardId(dashboard_id: UUID)  = {
    select.
      where(_.dashboard_id eqs dashboard_id).
      fetch()
  }

}