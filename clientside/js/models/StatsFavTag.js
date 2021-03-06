FlickrAssistant.Models.StatsFavTag = FlickrAssistant.ModelReadOnly.extend({
    "idAttribute": "tag"
});

FlickrAssistant.Collections.StatsFavTag = FlickrAssistant.CollectionReadOnly.extend({
    "nsid": null,
    "initialize": function (models, options) {
        if (!_.has(options, "nsid")) {
            throw new FlickrAssistant.Error("You have to pass `nsid` to create instance of FlickrAssistant.Collections.StatsFavTag.");
        }
        this.nsid = options.nsid;
    },
    "url": function() {
        return "/api/stats/favs/" + this.nsid + "/tags";
    },
    "model": FlickrAssistant.Models.StatsFavTag
});
