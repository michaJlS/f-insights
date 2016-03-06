package controllers

import com.websudos.phantom.connectors.KeySpaceBuilder
import infrastructure.cassandra.FlickrAssistantDb
import play.api.Play.current

trait Db
{

  protected def getKeySpaceDef() = {
    val host = current.configuration.getString("alerf.fa.cassandra.host").get
    val port = current.configuration.getInt("alerf.fa.cassandra.port").get
    val keyspace = current.configuration.getString("alerf.fa.cassandra.keyspace").get
    val user = current.configuration.getString("alerf.fa.cassandra.user").get
    val pass = current.configuration.getString("alerf.fa.cassandra.pass").get

    val kb = new KeySpaceBuilder(_.addContactPoint(host).withPort(port).withCredentials(user, pass))
    kb.keySpace(keyspace)
  }

  lazy val db = new FlickrAssistantDb(getKeySpaceDef())

}
