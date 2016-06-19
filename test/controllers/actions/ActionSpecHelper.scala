package controllers.actions

import play.api.libs.json.JsValue
import play.api.mvc.{AnyContentAsJson, Result}
import play.api.test.{FakeHeaders, FakeRequest}

import scala.concurrent.duration._
import scala.concurrent.{Future, Await}


trait ActionSpecHelper
{

  val duration = 500 milliseconds

  protected def composeFakeRequest(headersSeq: (String, String)*) = new FakeRequest[String](method = "GET", uri = "", new FakeHeaders(headersSeq), body = "")

  protected def composeFakeRequest(body:JsValue, headersSeq: (String, String)*) = new FakeRequest[AnyContentAsJson](
    method = "GET",
    uri = "",
    new FakeHeaders(headersSeq),
    AnyContentAsJson(body)
  )


  protected def awaitResult[T](result: Future[T]): T = {
    Await.result(result, duration)
  }

}
