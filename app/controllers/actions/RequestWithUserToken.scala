package controllers.actions

import domain.entities.UserToken
import play.api.mvc.{Request, WrappedRequest}

class RequestWithUserToken[A](val token:UserToken, val userNsid:String, request: Request[A]) extends WrappedRequest[A](request)
