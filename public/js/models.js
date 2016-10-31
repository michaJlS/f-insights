

FlickrAssistant.Models.Dashboard = FlickrAssistant.Model.extend({
    "urlRoot": "api/dashboard",
    "idAttribute": "nsid",
    forceCreate: function(options = null) {
        var o = new FlickrAssistant.Models.Dashboard({nsid: this.nsid});
        o.isNew = function() {return true;};
        o.save(options);
    },
    createNew: function(options) {
        this.sync("create", this, options);
    },
    fetchWithFallback: function() {
        // TODO pass options to create new, somehow
        // and refresh other boxes
        this.fetch({
            error: (function() {
                this.createNew({
                    success: (function() {

                        this.fetch();
                     }).bind(this)
                });
            }).bind(this)
        });
    }
});


FlickrAssistant.Models.UserInfo = FlickrAssistant.Model.extend({
    "urlRoot": "api/user/info",
    "idAttribute": "nsid"
});

FlickrAssistant.Collections.UserInfo = FlickrAssistant.Collection.extend({
    "url": "api/user/info",
    "model": FlickrAssistant.Models.UserInfo
});

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
    "model": FlickrAssistant.Models.StatsFavTag,
    "comparator": function(a, b) {
          var ac = parseInt(a.get("count"));
          var bc = parseInt(b.get("count"));
          if (ac == bc) return 0;
          if (ac > bc) return -1;
          return 1;
    }
});

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