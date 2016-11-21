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