package controllers

import play.api.Play.current
import play.api.libs.oauth.{OAuth, ServiceInfo, ConsumerKey}

/**
 *
 */
trait Flickr
{

  val consumerKey = new ConsumerKey(current.configuration.getString("alerf.fa.app_key").get, current.configuration.getString("alerf.fa.app_secret").get)

  val serviceInfo = new ServiceInfo(
    current.configuration.getString("alerf.flickr.oauth.request_token").get,
    current.configuration.getString("alerf.flickr.oauth.access_token").get,
    current.configuration.getString("alerf.flickr.oauth.authorize").get,
    consumerKey)

  val oauth = new OAuth(serviceInfo, true)

}
