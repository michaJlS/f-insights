FlickrAssistant.Views.TopFavedAuthors = FlickrAssistant.BaseView.extend({
    owners: null,
    contacts: null,
    nonContacts: false,
    template: "top-faved-authors",
    events: {
        "click .non-contacts": "switchToNonContacts",
        "click .remove-contacts-filter": "removeContactsFilter"
    },
    initialize: function () {
        this.contacts = new FlickrAssistant.Collections.Contact(null, {"nsid": FlickrAssistant.Context.nsid});
        this.owners = new FlickrAssistant.Collections.StatsFavOwner(null, {"nsid": FlickrAssistant.Context.nsid});
        // TODO redundant with header ... share somehow
        this.contacts.fetch();
        this.owners.fetch({
            success: this.render.bind(this)
        });
    },
    removeContactsFilter: function(e) {
        e.stopImmediatePropagation();
        this.nonContacts = false;
        this.render();
        return false;
    },
    switchToNonContacts: function(e) {
        e.stopImmediatePropagation();
        this.nonContacts = true;
        this.render();
        return false;
    },
    serialize: function () {
         var json = this.owners.toJSON();
         var contactsIds = this.contacts.pluck("nsid");
         if (this.nonContacts) {
            json = json.filter(function(obj) {
                return -1 === contactsIds.indexOf(obj.owner)
            });
         }
         return {
             owners: json.slice(0, 10),
             nonContacts: this.nonContacts
         };
    }
});
FlickrAssistant.Views.FavedTagsList = FlickrAssistant.BaseView.extend({
    template: "faved-tags-list",
    tags: [],
    initialize: function (options) {
        this.tags = options.tags
    },
    serialize: function () {
        return {
            tags: this.tags
        }
    }
});
FlickrAssistant.Views.TopFavedTags = FlickrAssistant.BaseView.extend({
    template: "top-faved-tags",
    tags: [],
    page: 0,
    displayedPics: [],
    events: {
        "click .moar": "onAddPage"
    },
    initialize: function () {
        this.tags = new FlickrAssistant.Collections.StatsFavTag(null, {"nsid": FlickrAssistant.Context.nsid});
        this.tags.fetch({
            success: this.addPage.bind(this),
            data: {threshold: 20}
        });
    },
    onAddPage: function(e) {
        e.stopImmediatePropagation();
        this.addPage();
        return false;
    },
    addPage: function() {
        var from = function(x){ return 10 * (x-1); },
            to = function(x) { return 10 * x; },
            tagsJSON = [], child = null;

        if (to(this.page) >= this.tags.length) {
            return false;
        }

        ++this.page;
        tagsJSON = this.tags.toJSON().slice(from(this.page), to(this.page));
        child = new FlickrAssistant.Views.FavedTagsList({
            tags: this.decorate(tagsJSON)
        });
        this.setView(".pages", child, true);
        child.render();

//
//        if (_.has(options, "header")) {
//            this.setView("#header", options.header, true)
//        }

    },
    decorate: function(tagsJSON) {
        var l = tagsJSON.length, i = 0, j = 0, k = 0, id = "";

        for (i = 0; i<l; i++) {
            j = 0; k = tagsJSON[i]["photos"].length;
            tagsJSON[i].topphotos = [];
            while (tagsJSON[i].topphotos.length < 3 && j < k) {
                id = tagsJSON[i]["photos"][j]["photo"]["id"]
                if (-1 === this.displayedPics.indexOf(id)) {
                    tagsJSON[i].topphotos.push(tagsJSON[i]["photos"][j]);
                    this.displayedPics.push(id);
                }
                ++j;
            }
        }

        return tagsJSON;
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
        this.contacts = new FlickrAssistant.Collections.Contact(null, {"nsid": FlickrAssistant.Context.nsid});
        this.dashboard = new FlickrAssistant.Models.Dashboard({"nsid": FlickrAssistant.Context.nsid});
        this.userInfo.fetch();
        this.contacts.fetch();
        this.dashboard.fetchWithFallback();
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
