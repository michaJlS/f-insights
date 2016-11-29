FlickrAssistant.Models.Relative = FlickrAssistant.ModelReadOnly.extend({
    "idAttribute": "nsid"
});

FlickrAssistant.Collections.Relative = FlickrAssistant.CollectionReadOnly.extend({
    "user": null,
    "initialize": function (models, options) {
        if (!_.has(options, "user")) {
            throw new FlickrAssistant.Error("You have to pass `user` to create instance of FlickrAssistant.Collections.Relative.");
        }
        this.user = options.user;
    },
    "url": function() {
        return "/api/stats/user/" + this.user + "/relatives";
    },
   "model": FlickrAssistant.Models.Relative
});
