FlickrAssistant.Models.FavingUserStats = FlickrAssistant.ModelReadOnly.extend({
    "idAttribute": "user"
});
FlickrAssistant.Collections.FavingUserStats = FlickrAssistant.CollectionReadOnly.extend({
   "nsid": null,
   "initialize": function (models, options) {
       if (!_.has(options, "nsid")) {
           throw new FlickrAssistant.Error("You have to pass `nsid` to create instance of FlickrAssistant.Collections.FavingUserStats.");
       }
       this.nsid = options.nsid;
   },
   "url": function() {
       return "/api/stats/user/" + this.nsid + "/faving";
   },
  "model": FlickrAssistant.Models.FavingUserStats
});
