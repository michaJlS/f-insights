FlickrAssistant.Views.TopFavedAuthors = FlickrAssistant.BaseView.extend({
    owners: null,
    template: "top-faved-authors",
    initialize: function () {
        this.owners = new FlickrAssistant.Collections.StatsFavOwner(null, {"nsid": FlickrAssistant.Context.nsid});
        this.owners.fetch({async: false});
    },
    serialize: function () {
         return {
             owners: this.owners.toJSON().slice(0, 10)
         };
    }
});

FlickrAssistant.Views.TopFavedTags = FlickrAssistant.BaseView.extend({
    tags: null,
    template: "top-faved-tags",
    initialize: function () {
        this.tags = new FlickrAssistant.Collections.StatsFavTag(null, {"nsid": FlickrAssistant.Context.nsid});
        this.tags.fetch({async: false});
    },
    serialize: function () {
         return {
             tags: this.tags.toJSON().slice(0, 10)
         };
    }
});

FlickrAssistant.Views.Home = FlickrAssistant.BaseView.extend({
    template: "home",
    initialize: function () {
        this.setView(".faved-authors", new FlickrAssistant.Views.TopFavedAuthors(), true)
        this.setView(".faved-tags", new FlickrAssistant.Views.TopFavedTags(), true)
    }
});

FlickrAssistant.Views.Header = FlickrAssistant.BaseView.extend({
    template: "header",
    userInfo: null,
    contacts: null,
    dashboard: null,
    initialize: function () {
        this.userInfo = new FlickrAssistant.Models.UserInfo({"nsid": FlickrAssistant.Context.nsid});
        this.userInfo.fetch({async: false});

        this.dashboard = new FlickrAssistant.Models.Dashboard({"nsid": FlickrAssistant.Context.nsid});
        this.userInfo.fetch({async: false});

        this.contacts = new FlickrAssistant.Collections.Contact(null, {"nsid": FlickrAssistant.Context.nsid});
        this.contacts.fetch({async: false});


    },
    serialize: function () {
        return {
            user: this.userInfo.toJSON()
        };
    }
});

FlickrAssistant.Views.Nav = FlickrAssistant.BaseView.extend({
    template: "nav"
});

FlickrAssistant.Views.Layout = FlickrAssistant.BaseView.extend({
    initialize: function(options) {
        if (_.has(options, "header")) {
            this.setView("#header", options.header, true)
        }
        if (_.has(options, "content")) {
            this.setView("#main", options.content, true)
        }
        if (_.has(options, "menu")) {
            this.setView("#menu", options.menu, true)
        }
    },
    template: "layout"
});
