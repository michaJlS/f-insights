package models.flickr

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.libs.json._

import play.api.test._
import play.api.test.Helpers._


@RunWith(classOf[JUnitRunner])
class ResponseParserSpec extends Specification
{

  val parser = new ResponseParser

  val emptyString = Json.parse(SampleInvalidResponses.emptyString)
  val invalidJson = Json.parse(SampleInvalidResponses.dummy)
  val tokenInfoMissingNsid = Json.parse(SampleInvalidResponses.tokeInfoNoNsid)
  val validTokenInfo = Json.parse(SampleValidResponses.tokenInfo)
  val personInfoMissingRealname = Json.parse(SampleInvalidResponses.personInfoMissingRealname)
  val validPersonInfo = Json.parse(SampleValidResponses.personInfo)
  val emptyPhotosList = Json.parse(SampleValidResponses.emptyPhotosList)
  val emptyPhotosListMissingTotal = Json.parse(SampleInvalidResponses.emptyPhotosListMissingTotal)
  val favsPhotosList = Json.parse(SampleValidResponses.favs)


  "ResponseParser#getTokenInfo()" should {
    "return None if provided json" in {
      "is an empty string" in {
        parser.getTokenInfo(emptyString) must beNone
      }
      "is definitely different than it should be" in {
        parser.getTokenInfo(invalidJson) must beNone
      }
      "is almost ok but it is missing NSID information" in {
        parser.getTokenInfo(tokenInfoMissingNsid) must beNone
      }
    }
    "return TokenInfo object wrapped with Some if provided json is ok" in {
      parser.getTokenInfo(validTokenInfo) must beSome[TokenInfo]
    }
  }

  "ResponseParser#getUserInfo()" should {
    "return None if provided json" in {
      "is an empty string" in {
        parser.getUserInfo(emptyString) must beNone
      }
      "is definitely different than it should be" in {
        parser.getUserInfo(invalidJson) must beNone
      }
      "is almost ok but it is missing NSID information" in {
        parser.getTokenInfo(personInfoMissingRealname) must beNone
      }
    }
    "return UserInfo object wrapped with Some if provided json is ok" in {
      parser.getUserInfo(validPersonInfo) must beSome[UserInfo]
    }
  }

  "ResponseParser#getPhotosCollectionInfo()" should {
    "return None if provided json" in {
      "is an empty string" in {
        parser.getPhotosCollectionInfo(emptyString) must beNone
      }
      "is definitely different than it should be" in {
        parser.getPhotosCollectionInfo(invalidJson) must beNone
      }
      "is almost ok but it is missing total information" in {
        parser.getPhotosCollectionInfo(emptyPhotosListMissingTotal) must beNone
      }
    }
    "return UserInfo object wrapped with Some if provided json is" in {
      "a valid empty photos list" in {
        parser.getPhotosCollectionInfo(emptyPhotosList) must beSome[CollectionInfo]
      }
      "a valid favs list" in {
        parser.getPhotosCollectionInfo(favsPhotosList) must beSome[CollectionInfo]
      }
    }
  }


}
