FlickrAssistant.Views.TopFavedAuthors = FlickrAssistant.BaseView.extend({
    owners: null,
    ownersNoContacts: null,
    nonContacts: false,
    page: 0,
    template: "top-faved-authors",
    d: false,
    events: {
        "click .non-contacts": "switchToNonContacts",
        "click .remove-contacts-filter": "removeContactsFilter",
        "click .moar": "onAddPage"
    },
    onAddPage: function(e) {
        e.stopImmediatePropagation();
        this.addPage();
        return false;
    },
    loadData: function (from, to) {
        var d = "" + from + to;
        if (d === this.d) {
            return;
        }
        this.d = d;

        this.page = 0;
        this.owners.reset();
        this.ownersNoContacts.reset();
        this.render();
        this.owners.fetch({
            success: this.addPage.bind(this),
            data: {threshold: 5, from_timestamp: from, to_timestamp: to}
        });
        this.ownersNoContacts.fetch({data:{non_contacts: true, from_timestamp: from, to_timestamp: to}});

    },
    initialize: function () {
        this.owners = new FlickrAssistant.Collections.StatsFavOwner(null, {"nsid": FlickrAssistant.Context.nsid});
        this.ownersNoContacts = new FlickrAssistant.Collections.StatsFavOwner(null, {"nsid": FlickrAssistant.Context.nsid});
        this.loadData("", "");
    },
    removeContactsFilter: function(e) {
        e.stopImmediatePropagation();
        this.nonContacts = false;
        this.page = 0;
        this.render();
        this.addPage();
        return false;
    },
    switchToNonContacts: function(e) {
        e.stopImmediatePropagation();
        this.nonContacts = true;
        this.page = 0;
        this.render();
        this.addPage();
        return false;
    },
    addPage: function() {
        var from = function(x) { return 10 * (x-1); },
            to = function(x) { return 10 * x; },
            data = this.nonContacts ? this.ownersNoContacts : this.owners,
            child = null, dataJson = null;

        if (to(this.page) >= data.length) {
            return false;
        }
        ++this.page;
        dataJson = data.toJSON().slice(from(this.page), to(this.page));
        child = new FlickrAssistant.Views.FavedAuthorsList({
            owners: dataJson
        });
        this.setView(".pages", child, true);
        child.render();
    },
    serialize: function () {
         return {
             nonContacts: this.nonContacts
         };
    },
    dateRangeChanged: function(from, to) {
        this.loadData(from, to);
    }
});

FlickrAssistant.Views.FavedAuthorsList = FlickrAssistant.BaseView.extend({
     template: "faved-authors-list",
     owners: [],
     initialize: function (options) {
         this.owners = options.owners
     },
     serialize: function () {
         return {
             owners: this.owners
         }
     }
});
