FlickrAssistant.Views.MyHotTags = FlickrAssistant.BaseView.extend(FlickrAssistant.composeViews(
    FlickrAssistant.Components.ProtoPaginated,
    {
        template: "my-hot-tags",
        tags: null,
        defaultOrder: "top_avg_points",
        order: "",
        events: {
            "click .orderlink": "reorder"
        },
        initialize: function () {
            this.order = this.defaultOrder
            this.initPaginator(10);
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
            this.render();
            this.state = this.paginator.reset(FlickrAssistant.Components.TopPhotos.decorate(this.sort(this.tags.toJSON())));
            this.addPage();
        },
        reorder: function(e) {
            var newOrder = this.$(e.currentTarget).data("order")
            e.stopImmediatePropagation()
            if (this.order === newOrder) {
                return false;
            }
            this.order = newOrder
            this.$('a.orderlink').removeClass('active')
            this.$(e.currentTarget).addClass('active')
            this.dataReloaded()
            return false
        },
        sort: function(coll) {
            var orderKey = this.order;
            if (orderKey === this.defaultOrder) {
                return coll;
            }
            return coll.sort(function (a, b) {
                if (a[orderKey] === b[orderKey]) return 0;
                if (a[orderKey] < b[orderKey]) return 1;
                return -1;
            });
        },
        afterRender: function() {
            this.$('a.orderlink').removeClass('active')
            this.$('a.order_' + this.order).addClass('active')
        }
    }
));
