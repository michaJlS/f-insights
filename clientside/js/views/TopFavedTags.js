FlickrAssistant.Views.TopFavedTags = FlickrAssistant.BaseView.extend(FlickrAssistant.composeViews(
    FlickrAssistant.Components.ProtoPaginated,
    {
        template: "top-faved-tags",
        tags: [],
        d: false,
        displayedPics: [],
        initialize: function () {
            this.initPaginator(10);
            this.tags = new FlickrAssistant.Collections.StatsFavTag(null, {"nsid": FlickrAssistant.Context.nsid});
            this.loadData("", "");
        },
        loadData: function (from, to) {
            var d = "" + from + to;
            if (d === this.d) {
                return;
            }
            this.d = d;
            this.displayedPics = [];
            this.tags.reset();
            this.tags.fetch({
                success: this.dataReloaded.bind(this),
                data: {threshold: 20, from_timestamp: from, to_timestamp: to}
            });
        },
        dataReloaded: function () {
            this.render();
            this.paginator.reset(FlickrAssistant.Components.TopPhotos.decorate(this.tags.toJSON()));
            this.addPage();
        },
        addPage: function() {
            child = new FlickrAssistant.Views.SimpleList({
                tpl: "faved-tags-list",
                items: this.paginator.getData()
            });
            this.setView(".pages", child, true);
            child.render();
        },
        dateRangeChanged: function(from, to) {
            this.loadData(from, to);
        }
    }
));
