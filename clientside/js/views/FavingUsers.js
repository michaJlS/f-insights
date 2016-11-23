FlickrAssistant.Views.FavingUsers = FlickrAssistant.BaseView.extend(FlickrAssistant.composeViews(
    FlickrAssistant.Components.ProtoPaginated,
    {
        template: "faving-users",
        stats: null,
        contacts: null,
        contactsData: [],
        loaded: 0,
        initialize: function () {
            this.initPaginator(15);
            this.stats = new FlickrAssistant.Collections.FavingUserStats(null, {"nsid": FlickrAssistant.Context.nsid});
            this.contacts = new FlickrAssistant.Collections.Contact(null, {"nsid": FlickrAssistant.Context.nsid});
            this.contacts.fetch({
                success: this.loadedData.bind(this)
            });
            this.stats.fetch({
                success: this.loadedData.bind(this)
            });
        },
        loadedData: function () {
            ++this.loaded;
            if (this.loaded>1) this.dataReloaded();
        },
        addPage: function () {
            child = new FlickrAssistant.Views.FavingUsersList({
                items: this.paginator.getData(),
                contacts: this.contactsData
            });
            this.setView(".pages", child, true);
            child.render();
        },
        dataReloaded: function () {
            this.contactsData = this.contacts.pluck("nsid")
            this.state = this.paginator.reset(this.stats.toJSON());
            this.addPage();
        }
    }
));

FlickrAssistant.Views.FavingUsersList = FlickrAssistant.BaseView.extend({
     template: "faving-users-list",
     items: [],
     contacts: [],
     initialize: function (options) {
        this.items = options.items
        this.contacts = options.contacts
     },
     serialize: function () {
         return {
             items: this.items,
             contacts: this.contacts
         }
     }
});