FlickrAssistant.Models.PhotoTagStats = FlickrAssistant.ModelReadOnly.extend({
                           "idAttribute": "tag"
                       });

FlickrAssistant.Collections.PhotoTagStats = FlickrAssistant.CollectionReadOnly.extend({
   "nsid": null,
   "initialize": function (models, options) {
       if (!_.has(options, "nsid")) {
           throw new FlickrAssistant.Error("You have to pass `nsid` to create instance of FlickrAssistant.PhotoTagStats.FavingUserStats.");
       }
       this.nsid = options.nsid;
   },
   "url": function() {
       return "/api/stats/user/" + this.nsid + "/tags";
   },
  "model": FlickrAssistant.Models.PhotoTagStats
});