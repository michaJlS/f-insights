FlickrAssistant.Views.FavingUsers = FlickrAssistant.BaseView.extend(FlickrAssistant.composeViews(
    FlickrAssistant.Components.ProtoPaginated,
    {
        template: "faving-users",
        stats: null,
        initialize: function () {
            this.initPaginator(15);
            this.stats = new FlickrAssistant.Collections.FavingUserStats(null, {"nsid": FlickrAssistant.Context.nsid});
            this.stats.fetch({
                success: this.dataReloaded.bind(this)
            });
        },
        addPage: function () {
            child = new FlickrAssistant.Views.SimpleList({
                tpl: "faving-users-list",
                items: this.paginator.getData()
            });
            this.setView(".pages", child, true);
            child.render();
        },
        dataReloaded: function () {
            this.state = this.paginator.reset(this.stats.toJSON());
            this.addPage();
        }
    }
));
