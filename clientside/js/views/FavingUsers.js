FlickrAssistant.Views.FavingUsers = FlickrAssistant.BaseView.extend({
    template: "faving-users",
    initialize: function () {
        this.stats = new FlickrAssistant.Collections.FavingUserStats(null, {"nsid": FlickrAssistant.Context.nsid});
        this.stats.fetch({
            success: this.render.bind(this)
        });
    },
    serialize: function() {
        return {stats: this.stats.toJSON().slice(0, 10)};
    }
});
