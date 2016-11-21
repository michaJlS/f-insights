FlickrAssistant.Models.StatsFavOwner = FlickrAssistant.ModelReadOnly.extend({
    "idAttribute": "owner",
    initialize: function (attributes, options) {
          this.set("topphotos", this.get("photos").slice(0, Math.min(3, this.get("photos").length)))
    }
});

FlickrAssistant.Collections.StatsFavOwner = FlickrAssistant.CollectionReadOnly.extend({
    "nsid": null,
    "initialize": function (models, options) {
        if (!_.has(options, "nsid")) {
            throw new FlickrAssistant.Error("You have to pass `nsid` to create instance of FlickrAssistant.Collections.StatsFavOwner.");
        }
        this.nsid = options.nsid;
    },
    "url": function() {
        return "/api/stats/favs/" + this.nsid + "/owners";
    },
   "model": FlickrAssistant.Models.StatsFavOwner,
   "comparator": function(a, b) {
        var ac = parseInt(a.get("count"));
        var bc = parseInt(b.get("count"));
        if (ac == bc) return 0;
        if (ac > bc) return -1;
        return 1;
   }
});
