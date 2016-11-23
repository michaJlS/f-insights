FlickrAssistant.Views.MyHotTags = FlickrAssistant.BaseView.extend(FlickrAssistant.composeViews(
    FlickrAssistant.Components.ProtoPaginated,
    {
        template: "my-hot-tags",
        tags: null,
        initialize: function () {
            this.initPaginator(15);
            this.tags = new FlickrAssistant.Collections.PhotoTagStats(null, {"nsid": FlickrAssistant.Context.nsid});
            this.tags.fetch({
                success: this.dataReloaded.bind(this)
            });
        },
        addPage: function () {
            child = new FlickrAssistant.Views.SimpleList({tpl: "my-hot-tags-list", items: this.paginator.getData()});
            this.setView(".pages", child, true);
            child.render();
        },
        dataReloaded: function () {
            this.state = this.paginator.reset(FlickrAssistant.Components.TopPhotos.decorate(this.tags.toJSON()));
            this.addPage();
        }
    }
));
