FlickrAssistant.Models.UserInfo = FlickrAssistant.Model.extend({
    "urlRoot": "api/user/info",
    "idAttribute": "nsid"
});

FlickrAssistant.Collections.UserInfo = FlickrAssistant.Collection.extend({
    "url": "api/user/info",
    "model": FlickrAssistant.Models.UserInfo
});
