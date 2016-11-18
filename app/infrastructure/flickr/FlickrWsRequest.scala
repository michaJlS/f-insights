package infrastructure.flickr

import play.api.libs.ws.WSRequest

class FlickrWsRequest(request: WSRequest) {

  def setApiMethod(method: String): WSRequest = request.withQueryString("method" -> method)

  def setQueryParams(params: Map[String, String]): WSRequest = params.foldLeft(request)((req, param) => req.withQueryString(param))

  def setOptionalQueryParams(params: Map[String, Option[String]]): WSRequest =
    params.foldLeft(request){
      case (req, (k, Some(v))) => req.withQueryString(k -> v)
      case (req, _) => req
    }

}

object FlickrWsRequest {
  implicit def asFlickrWsRequest(ws: WSRequest): FlickrWsRequest = new FlickrWsRequest(ws)
}
