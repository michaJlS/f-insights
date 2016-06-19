package controllers.actions


import models.flickr.{ApiRepository, UserToken, TokenInfo}
import org.junit.runner.RunWith
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.http.Status
import play.api.mvc.{Request, Result}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


@RunWith(classOf[JUnitRunner])
class AttachTokenInfoSpec extends Specification with ActionSpecHelper with Mockito
{

  "Refiner AttachTokenInfo" >> {

    "returns Unauthorized HTTP response when" >> {
      "an invalid UserToken has been provided" >> {
        val repository = mock[ApiRepository].defaultReturn(Future.successful(None))
        val refiner = createRefiner(repository)
        val request = new RequestWithUserToken[String](UserToken("token", "secret"), "nsid", composeFakeRequest())
        val result = awaitResult(refiner.publicRefine(request))
        result must beLeft[Result]
      }
    }

    "returns refined Request when" >> {
      "valid UserToken has been provided" >> {
        val repository = mock[ApiRepository].defaultReturn(Future.successful(Some(TokenInfo("nsid", "username", "fullname"))))
        val refiner = createRefiner(repository)
        val request = new RequestWithUserToken[String](UserToken("token", "secret"), "nsid", composeFakeRequest())
        val result = awaitResult(refiner.publicRefine(request))
        result must beRight[RequestWithTokenInfo[String]]
      }
    }

  }

  // refine() is protected in contract of play.api.mvc.ActionRefiner, that is extended by AttachTokenInfo
  // so we have to leave it that way and find a workaround. We could call it through invokeBlock,
  // but that is clumsy solution and cost more than adding a proxy.
  def createRefiner(repository: ApiRepository) = new AttachTokenInfo(repository) {
    def publicRefine[A](request: RequestWithUserToken[A]) = refine(request)
  }

}
