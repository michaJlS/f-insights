package models.flickr

object SampleValidResponses
{

  val tokenInfo =
    """
      |{
      |  "oauth" : {
      |    "token" : {
      |      "_content" : "11111111111111111-2222222222222222"
      |    },
      |    "perms" : {
      |      "_content" : "write"
      |    },
      |    "user" : {
      |      "nsid" : "27552342@N02",
      |      "username" : "michaJlS",
      |      "fullname" : "Michal Sznurawa"
      |    }
      |  },
      |  "stat" : "ok"
      |}
      |    """.stripMargin

  val personInfo =
    """
      |{
      |  "person" : {
      |    "id" : "27552342@N02",
      |    "nsid" : "27552342@N02",
      |    "ispro" : 1,
      |    "can_buy_pro" : 0,
      |    "iconserver" : "5585",
      |    "iconfarm" : 6,
      |    "path_alias" : "michajls",
      |    "has_stats" : "1",
      |    "expire" : "0",
      |    "username" : {
      |      "_content" : "michaJlS"
      |    },
      |    "realname" : {
      |      "_content" : "Michal Sznurawa"
      |    },
      |    "mbox_sha1sum" : {
      |      "_content" : "5072930a05cc3f8a0403e5a3476981d732fddace"
      |    },
      |    "location" : {
      |      "_content" : "London"
      |    },
      |    "timezone" : {
      |      "label" : "GMT: Dublin, Edinburgh, Lisbon, London",
      |      "offset" : "+00:00",
      |      "timezone_id" : "Europe/London"
      |    },
      |    "description" : {
      |      "_content" : "I'm an amateur photographer"
      |    },
      |    "photosurl" : {
      |      "_content" : "https://www.flickr.com/photos/michajls/"
      |    },
      |    "profileurl" : {
      |      "_content" : "https://www.flickr.com/people/michajls/"
      |    },
      |    "mobileurl" : {
      |      "_content" : "https://m.flickr.com/photostream.gne?id=27531994"
      |    },
      |    "photos" : {
      |      "firstdatetaken" : {
      |        "_content" : "2009-06-09 22:06:40"
      |      },
      |      "firstdate" : {
      |        "_content" : "1360698043"
      |      },
      |      "count" : {
      |        "_content" : 169
      |      },
      |      "views" : {
      |        "_content" : "2480"
      |      }
      |    }
      |  },
      |  "stat" : "ok"
      |}
      |
    """.stripMargin

  val emptyPhotosList =
    """
      |{ "photos": { "page": 1, "pages": 0, "perpage": 100, "total": 0,
      |    "photo": [
      |
      |    ] }, "stat": "ok" }
    """.stripMargin

  val favs =

    """
      |{ "photos": { "page": 100, "pages": "1253", "perpage": 3, "total": "3757",
      |    "photo": [
      |      { "id": "20216912073", "owner": "94951940@N04", "secret": "1111111111", "server": "5686", "farm": 6, "title": "Mystic Morning", "ispublic": 1, "isfriend": 0, "isfamily": 0, "date_faved": "1441915873" },
      |      { "id": "20647358024", "owner": "111325145@N07", "secret": "2222222222", "server": "752", "farm": 1, "title": "Wild flower meadow", "ispublic": 1, "isfriend": 0, "isfamily": 0, "date_faved": "1441834771" },
      |      { "id": "16414481383", "owner": "30540702@N06", "secret": "3333333333", "server": "8685", "farm": 9, "title": "Foggy morning", "ispublic": 1, "isfriend": 0, "isfamily": 0, "date_faved": "1441834737" }
      |    ] }, "stat": "ok" }
    """.stripMargin

}


object SampleInvalidResponses
{


  val emptyString =
    """
      |""
    """.stripMargin

  val dummy =
    """
      |{"z": 2, "y": "abc"}
    """.stripMargin


  val tokeInfoNoNsid = """
                         |{
                         |  "oauth" : {
                         |    "token" : {
                         |      "_content" : "11111111111111111-2222222222222222"
                         |    },
                         |    "perms" : {
                         |      "_content" : "write"
                         |    },
                         |    "user" : {
                         |      "username" : "michaJlS",
                         |      "fullname" : "Michal Sznurawa"
                         |    }
                         |  },
                         |  "stat" : "ok"
                         |}
                       """.stripMargin

  val personInfoMissingRealname =
    """
      |{
      |  "person" : {
      |    "id" : "27552342@N02",
      |    "nsid" : "27552342@N02",
      |    "ispro" : 1,
      |    "can_buy_pro" : 0,
      |    "iconserver" : "5585",
      |    "iconfarm" : 6,
      |    "path_alias" : "michajls",
      |    "has_stats" : "1",
      |    "expire" : "0",
      |    "username" : {
      |      "_content" : "michaJlS"
      |    },
      |    "mbox_sha1sum" : {
      |      "_content" : "5072930a05cc3f8a0403e5a3476981d732fddace"
      |    },
      |    "location" : {
      |      "_content" : "London"
      |    },
      |    "timezone" : {
      |      "label" : "GMT: Dublin, Edinburgh, Lisbon, London",
      |      "offset" : "+00:00",
      |      "timezone_id" : "Europe/London"
      |    },
      |    "description" : {
      |      "_content" : "I'm an amateur photographer"
      |    },
      |    "photosurl" : {
      |      "_content" : "https://www.flickr.com/photos/michajls/"
      |    },
      |    "profileurl" : {
      |      "_content" : "https://www.flickr.com/people/michajls/"
      |    },
      |    "mobileurl" : {
      |      "_content" : "https://m.flickr.com/photostream.gne?id=27531994"
      |    },
      |    "photos" : {
      |      "firstdatetaken" : {
      |        "_content" : "2009-06-09 22:06:40"
      |      },
      |      "firstdate" : {
      |        "_content" : "1360698043"
      |      },
      |      "count" : {
      |        "_content" : 169
      |      },
      |      "views" : {
      |        "_content" : "2480"
      |      }
      |    }
      |  },
      |  "stat" : "ok"
      |}
      |
    """.stripMargin


  val emptyPhotosListMissingTotal =
    """
      |{ "photos": { "page": 1, "pages": 0, "perpage": 100,
      |    "photo": [
      |
      |    ] }, "stat": "ok" }
    """.stripMargin



}
