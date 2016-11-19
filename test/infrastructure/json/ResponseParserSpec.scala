package infrastructure.json

import domain.entities.{CollectionInfo, TokenInfo, UserInfo}
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import test.Resources


@RunWith(classOf[JUnitRunner])
class ResponseParserSpec extends Specification with Resources
{

  val parser = new ResponseParser

  val emptyString = jsonResource("/json/EmptyString.json")
  val invalidJson = jsonResource("/json/SampleObject.json")
  val tokenInfoMissingNsid = jsonResource("/flickrapi/invalid/TokeInfoNoNsid.json")
  val validTokenInfo = jsonResource("/flickrapi/valid/TokenInfo.json")
  val personInfoMissingRealname = jsonResource("/flickrapi/invalid/PersonInfoMissingRealname.json")
  val validPersonInfo = jsonResource("/flickrapi/valid/PersonInfo.json")
  val emptyPhotosList = jsonResource("/flickrapi/valid/EmptyPhotoList.json")
  val emptyPhotosListMissingTotal = jsonResource("/flickrapi/invalid/EmptyPhotosListMissingTotal.json")
  val emptyPhotosListObjInsteadArray = jsonResource("/flickrapi/invalid/EmptyPhotosListObjInsteadArray.json")
  val favsPhotosList = jsonResource("/flickrapi/valid/Favs.json")
  val photoExcerptMissingId = jsonResource("/flickrapi/invalid/PhotoExcerptMissingId.json")
  val photoExcerptMissingTitle = jsonResource("/flickrapi/invalid/PhotoExcerptMissingTitle.json")
  val favsJustOneOk = jsonResource("/flickrapi/invalid/FavsJustOneOk.json")
  val photoFavsOk = jsonResource("/flickrapi/valid/Photofavs.json")

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

  "ResponseParser#getPhotoExcerpt()" should {
    "return None if provided json" in {
      "is an empty string" in {
        parser.getPhotoExcerpt(emptyString) must beNone
      }
      "is definitely different than it should be" in {
        parser.getPhotoExcerpt(invalidJson) must beNone
      }
      "is almost ok but it is missing Id information" in {
        parser.getPhotoExcerpt(photoExcerptMissingId) must beNone
      }
      "is almost ok but it is missing title information" in {
        parser.getPhotoExcerpt(photoExcerptMissingTitle) must beNone
      }
    }
  }

  "ResponseParser#getPhotos()" should {
    "return None if provided json" in {
      "is an empty string" in {
        parser.getPhotos(emptyString) must beNone
      }
      "is definitely different than it should be" in {
        parser.getPhotos(invalidJson) must beNone
      }
      "object instead of array" in {
        parser.getPhotos(emptyPhotosListObjInsteadArray) must beNone
      }
    }

    "return Some with: " in {
      "an empty list when there are no photos but provided json is ok" in {
        val l = parser.getPhotos(emptyPhotosList)
        l must beSome
        l.get.length must beEqualTo(0)
      }
      "return list with all photos if all are ok" in {
        val l = parser.getPhotos(favsPhotosList)
        l must beSome
        l.get.length must beEqualTo(3)
      }
      "returns list only with valid photos" in {
        val l = parser.getPhotos(favsJustOneOk)
        l must beSome
        l.get.length must beEqualTo(1)
      }
    }
  }

  "ResponseParser#getFavourites()" should {
    "return list of valid photo favs" in {
      val l = parser.getPhotoFavourites(photoFavsOk, "someowner")
      l must beSome
      l.get.length must beEqualTo(3)
    }
  }


}
