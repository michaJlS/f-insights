package infrastructure.cassandra.table

import com.websudos.phantom.dsl._
import models.flickr.AppUserDetail


sealed class AppUserDetails extends CassandraTable[AppUserDetails, AppUserDetail]
{

  override lazy val tableName = "app_user_details"

  object nsid extends StringColumn(this) with PartitionKey[String]
  object detail_key extends StringColumn(this) with ClusteringOrder[String]
  object detail_value extends StringColumn(this)

  override def fromRow(r: Row) = AppUserDetail(nsid(r), detail_key(r), detail_value(r))

}

abstract class ConcreteAppUserDetails extends AppUserDetails with RootConnector
{

  def insertDetail(detail: AppUserDetail) = {
    insert
      .value(_.nsid, detail.nsid)
      .value(_.detail_key, detail.detail_key)
      .value(_.detail_value, detail.detail_value)
      .future()
  }

  def getDetail(nsid: String, key: String) = {
    select.where(_.nsid eqs nsid).and(_.detail_key eqs key).one
  }

}
