package modules

import com.google.inject.{AbstractModule, Provides}
import com.google.inject.name.{Named, Names}
import com.websudos.phantom.connectors.{KeySpaceBuilder, KeySpaceDef}
import infrastructure.cassandra.FlickrAssistantDb
import infrastructure.flickr.{ApiClient, ApiRepository}
import infrastructure.json.ResponseParser
import play.api.{Configuration, Environment}
import play.api.libs.oauth.{ConsumerKey, OAuth, ServiceInfo}
import play.api.libs.ws.WSClient

class FlickrAssistantModule(environment: Environment, configuration: Configuration) extends AbstractModule
{

  override def configure(): Unit = {
    // nothing to do here
    // https://www.playframework.com/documentation/2.5.x/ScalaDependencyInjection
    // https://www.playframework.com/documentation/2.5.x/ScalaTestingWithGuice
  }

  @Provides @Named("FlickrOAuth")
  def ouathProvider() : OAuth = new OAuth(getFlickrServiceInfo, true)

  @Provides
  def dbProvider(): FlickrAssistantDb = {
    new FlickrAssistantDb(getKeySpaceDef())
  }

  @Provides
  def apiRepositoryProvider(apiClient: WSClient):ApiRepository = {
    new ApiRepository(getFlickrApiClient(apiClient), new ResponseParser)
  }

  private def getFlickrApiClient(apiClient: WSClient) = {
    new ApiClient(configuration.getString("alerf.flickr.rest.url").get, apiClient, getFlickrConsumerKey)
  }

  private def getFlickrConsumerKey = new ConsumerKey(
    configuration.getString("alerf.fa.app_key").get,
    configuration.getString("alerf.fa.app_secret").get
  )

  private def getFlickrServiceInfo =  new ServiceInfo(
    configuration.getString("alerf.flickr.oauth.request_token").get,
    configuration.getString("alerf.flickr.oauth.access_token").get,
    configuration.getString("alerf.flickr.oauth.authorize").get,
    getFlickrConsumerKey
  )

  private def getKeySpaceDef() = {
    val host = configuration.getString("alerf.fa.cassandra.host").get
    val port = configuration.getInt("alerf.fa.cassandra.port").get
    val keyspace = configuration.getString("alerf.fa.cassandra.keyspace").get
    val user = configuration.getString("alerf.fa.cassandra.user").get
    val pass = configuration.getString("alerf.fa.cassandra.pass").get

    val kb = new KeySpaceBuilder(_.addContactPoint(host).withPort(port).withCredentials(user, pass))
    kb.keySpace(keyspace)
  }

}
