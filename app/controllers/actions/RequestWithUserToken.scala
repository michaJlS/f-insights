package controllers.actions

import models.flickr.UserToken
import play.api.mvc.{Request, WrappedRequest}

class RequestWithUserToken[A](val token:UserToken, val userNsid:String, request: Request[A]) extends WrappedRequest[A](request)
