FlickrAssistant.Views.TopFavedAuthors = FlickrAssistant.BaseView.extend(FlickrAssistant.composeViews(
    FlickrAssistant.Components.ProtoPaginated,
    {
        owners: null,
        ownersNoContacts: null,
        nonContacts: false,
        template: "top-faved-authors",
        d: false,
        events: {
            "click .non-contacts": "switchToNonContacts",
            "click .remove-contacts-filter": "removeContactsFilter"
        },
        initialize: function () {
            this.initPaginator(10);
            this.owners = new FlickrAssistant.Collections.StatsFavOwner(null, {"nsid": FlickrAssistant.Context.nsid});
            this.ownersNoContacts = new FlickrAssistant.Collections.StatsFavOwner(null, {"nsid": FlickrAssistant.Context.nsid});
            this.loadData("", "");
        },
        loadData: function (from, to) {
            var d = "" + from + to;
            if (d === this.d) {
                return;
            }
            this.d = d;
            this.owners.reset();
            this.ownersNoContacts.reset();
            this.owners.fetch({
                success: this.dataReloaded.bind(this),
                data: {threshold: 5, from_timestamp: from, to_timestamp: to}
            });
            this.ownersNoContacts.fetch({data:{non_contacts: true, from_timestamp: from, to_timestamp: to}});
        },
        contactsFilterSwitch: function(e) {
            e.stopImmediatePropagation();
            this.dataReloaded();
            return false;
        },
        removeContactsFilter: function(e) {
            this.nonContacts = false;
            return this.contactsFilterSwitch(e);
        },
        switchToNonContacts: function(e) {
            this.nonContacts = true;
            return this.contactsFilterSwitch(e);
        },
        dataReloaded: function () {
            var data = this.nonContacts ? this.ownersNoContacts.toJSON() : this.owners.toJSON();
            this.render();
            this.paginator.reset(data);
            this.addPage();
        },
        addPage: function() {
            child = new FlickrAssistant.Views.SimpleList({tpl: "faved-authors-list", items: this.paginator.getData()});
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
    }
));