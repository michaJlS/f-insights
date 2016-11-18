package infrastructure.flickr

import play.api.libs.ws.WSResponse

class FlickrWsResponse(response: WSResponse) {

  def isSuccess() = response.status > 199 && response.status < 300

}

object FlickrWsResponse {
  implicit def asFlickrWsResponse(response: WSResponse): FlickrWsResponse = new FlickrWsResponse(response)
}