FlickrAssistant.Views.DateRange = FlickrAssistant.BaseView.extend({
    template: "daterange",
    filterChange: function(){},
    fid: 0,
    initialize: function(options) {
        this.filterChange = options.filterChange;
        this.fid = 'form-daterange-' + Math.random() * 10000 + '-' + Math.random() * 10000 ;
    },
    events: {
        "submit .daterange": "onSubmit"
    },
    serialize: function() {
        return {fid: this.fid};
    },
    isDate: function(x) { return x.match(/^20[0-9][0-9]-[01][0-9]-[0-3][0-9]$/); },
    ok: function(x) { return (x === "") || this.isDate(x); },
    f:  function(n){ return this.$('[name=' + n + ']'); },
    fv: function(n) {
            var f = this.f(n);
            var v = f.val().trim();
            var ok = this.ok(v);
            return {"f": f, "v": v, "ok": ok, "n": n };
        },
    feedback: function(x) {
        if (x.ok) {
            x.f.parent('div').removeClass('has-error');
            return;
        }
        x.f.parent('div').addClass('has-error');
    },
    ts: function(v) {
        var m = v.match(/(\d+)-0*(\d+)-0*(\d+)/);
        if ("" === v) {
            return v;
        }
        return Math.round((new Date(m[1], m[2], m[3])).getTime()/1000);
    },
    decorate: function(x) { x.d = this.ts(x.v) },
    onSubmit: function() {
        var data = ['from', 'to'].map(this.fv.bind(this));
        data.forEach(this.feedback.bind(this));
        if (!data.reduce(function(x, y){return {ok: x.ok && y.ok}; }, {ok: true}).ok) {
            return false;
        }
        data.forEach(this.decorate.bind(this));
        this.filterChange(data[0].d, data[1].d);

        return false;
    }
});

FlickrAssistant.Views.Timeline = FlickrAssistant.BaseView.extend({
    stats: null,
    template: "timeline",
    initialize: function () {
        this.stats = new FlickrAssistant.Collections.MonthlyStats(null, {"nsid": FlickrAssistant.Context.nsid});
        this.stats.fetch({
            success: this.render.bind(this)
        });
    },
    serialize: function () {
    console.log(this.stats.dataByYear());
        var stats = [];
        if (this.stats) {
            stats = this.stats.dataByYear();
            return {
                "stats": stats,
                "months": this.stats.months(),
                "noStats": stats.length < 1,
                "cssArr": ["odd", "even"]
            };
        } else {
            return {
                "noStats": true
            };
        }
    }
});

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
    d: false,
    displayedPics: [],
    events: {
        "click .moar": "onAddPage"
    },
    loadData: function (from, to) {
        var d = "" + from + to;
        if (d === this.d) {
            return;
        }
        this.d = d;
        this.displayedPics = [];
        this.page = 0;
        this.tags.reset();
        this.render();
        this.tags.fetch({
            success: this.addPage.bind(this),
            data: {threshold: 20, from_timestamp: from, to_timestamp: to}
        });

    },
    initialize: function () {
        this.tags = new FlickrAssistant.Collections.StatsFavTag(null, {"nsid": FlickrAssistant.Context.nsid});
        this.loadData("", "");
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
    },
     dateRangeChanged: function(from, to) {
         this.loadData(from, to);
     }
});

FlickrAssistant.Views.Home = FlickrAssistant.BaseView.extend({
    template: "home",
    initialize: function () {
        this.setView(".faved-filters", new FlickrAssistant.Views.DateRange({
            filterChange: this.onFilterChange.bind(this)
        }), true)
        this.setView(".faved-authors", new FlickrAssistant.Views.TopFavedAuthors(), true)
        this.setView(".faved-tags", new FlickrAssistant.Views.TopFavedTags(), true)
        this.setView("#timeline", new FlickrAssistant.Views.Timeline(), true)
    },
    onFilterChange: function(from, to) {
        ['.faved-authors', '.faved-tags'].map(this.getView.bind(this)).forEach(function (vw) { vw.dateRangeChanged(from, to); });
    }
});

FlickrAssistant.Views.Header = FlickrAssistant.BaseView.extend({
    template: "header",
    userInfo: null,
    contacts: null,
    dashboard: null,
    events:  {
        "click .update-dashboard": "updateDashboard"
    },
    initialize: function () {
        this.userInfo = new FlickrAssistant.Models.UserInfo({"nsid": FlickrAssistant.Context.nsid});
        this.contacts = new FlickrAssistant.Collections.Contact(null, {"nsid": FlickrAssistant.Context.nsid});
        this.dashboard = new FlickrAssistant.Models.Dashboard({"nsid": FlickrAssistant.Context.nsid});
        this.userInfo.fetch();
        this.contacts.fetch();
        this.dashboard.fetchWithFallback();
    },
    updateDashboard: function () {
        var updatedDashboard = new FlickrAssistant.Models.Dashboard({"nsid": FlickrAssistant.Context.nsid})
        updatedDashboard.createNew({success: function(){
            location.reload();
        }});
        return false;
    },
    serialize: function () {
        return {
            user: this.userInfo.toJSON()
        };
    }
});

FlickrAssistant.Views.Nav = FlickrAssistant.BaseView.extend({
    events: {
        "click a": "navToTab"
    },
    navToTab: function(e) {
        e.preventDefault();
        jQuery(e.currentTarget).tab("show");
    },
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
