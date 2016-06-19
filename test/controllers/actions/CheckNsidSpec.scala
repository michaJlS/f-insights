package controllers.actions

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.http.Status
import play.api.mvc.Result


@RunWith(classOf[JUnitRunner])
class CheckNsidSpec extends Specification with ActionSpecHelper
{

  "Filter CheckNsid" >> {

    "returns BadRequest when" >> {
      "empty `nsid` has been provided" >> {
        val filter = new CheckNsid("")
        val result = awaitResult(filter.filter(composeFakeRequest()))
        result must beSome[Result]
        result.get.header.status must beEqualTo(Status.BAD_REQUEST)
      }
    }

    "doesn't return HTTP response when" >> {
      "non empty `nsid` has been provided" >> {
        val filter = new CheckNsid("sampl@nsid")
        val result = awaitResult(filter.filter(composeFakeRequest()))
        result must beNone
      }
    }

  }

}
