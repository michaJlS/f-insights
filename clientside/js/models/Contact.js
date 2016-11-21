FlickrAssistant.Models.Contact = FlickrAssistant.ModelReadOnly.extend({
    "idAttribute": "nsid"
});

FlickrAssistant.Collections.Contact = FlickrAssistant.CollectionReadOnly.extend({
    "nsid": null,
    "initialize": function (models, options) {
        if (!_.has(options, "nsid")) {
            throw new FlickrAssistant.Error("You have to pass `nsid` to create instance of FlickrAssistant.Collections.Contact.");
        }
        this.nsid = options.nsid;
    },
    "url": function() {
        return "/api/user/contacts/" + this.nsid;
    },
   "model": FlickrAssistant.Models.Contact
});
