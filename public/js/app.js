//Backbone.Layout.configure({ manage: true });
//var layout = new Backbone.Layout({ template: "#layout" });
Backbone.Layout.configure({});


Handlebars.registerHelper("debug", function(optionalValue) {
  console.log("Current Context");
  console.log("====================");
//  console.log(this);

  if (optionalValue) {
    console.log("Value");
    console.log("====================");
    console.log(optionalValue);
  }
});


var FlickrAssistant = {};
FlickrAssistant.Models = {};
FlickrAssistant.Views = {};
FlickrAssistant.Collections = {};
FlickrAssistant.Templates = {};
FlickrAssistant.Context = {
    nsid: "",
    s: "",
    t: ""
};

FlickrAssistant.Error = _.extend(Error, {});

FlickrAssistant.debug = function (msg) {
    console.log(msg);
}

FlickrAssistant.BaseView = Backbone.Layout.extend({
    fetchTemplate: function (path) {
        return _.has(FlickrAssistant.Templates, path) ?
                    FlickrAssistant.Templates[path] :
                    function() {
                        FlickrAssistant.debug("Undefined template: " + path);
                        return "";
                    };
    }
});

FlickrAssistant.Persist = {

    sync: function(obj, args) {
        if (_.has(args, 2)) {
            _.has(args[2], "headers") || (args[2]["headers"] = {});
            args[2]["headers"]["fa_nsid"] = FlickrAssistant.Context.nsid;
            args[2]["headers"]["fa_secret"] = FlickrAssistant.Context.s;
            args[2]["headers"]["fa_token"] = FlickrAssistant.Context.t;
        } else {
            FlickrAssistant.debug("FlickrAssistant.Persist: args has no el 2!");
        }
        return Backbone.sync.apply(obj, args);
    }

};

FlickrAssistant.Collection = Backbone.Collection.extend({
    sync: function() {
        return FlickrAssistant.Persist.sync(this, arguments);
    }
});

FlickrAssistant.CollectionReadOnly = FlickrAssistant.Collection.extend({
//    sync: function () {
////        throw new FlickrAssistant.Error("Can not execute a sync method on a read-only collection.");
//    }
});

FlickrAssistant.Model = Backbone.Model.extend({
    sync: function() {
        return FlickrAssistant.Persist.sync(this, arguments);
    }
});

FlickrAssistant.ModelReadOnly = FlickrAssistant.Model.extend({
//    sync: function () {
////        throw new FlickrAssistant.Error("Can not execute a sync method on a read-only model.");
//    }
});

//FlickrAssistant.Controller = function () {};


FlickrAssistant.Config = {


    options: {},
    defaults: {},

    setOption: function (key, val) {
        this.options[key] = val;
    },

    getOption: function (key) {
        if (!_.has(this.options, key)) {
            FlickrAssistant.debug("Undefined config option: " + key);
            return null;
        }
        return this.options[key];
    },

    init: function (vals) {
        _.extend(this.options, this.defaults);
        _.extend(this.options, vals);
    }

};

FlickrAssistant.App = function(config, context, templates) {

    var that = this;
    var layout = null

    function initLayout() {
        layout = new FlickrAssistant.Views.Layout({
            el: "#layout",
            header: new FlickrAssistant.Views.Header(),
            content: new FlickrAssistant.Views.Home()
        });
    }

    function initTemplates() {
        FlickrAssistant.Templates = templates || {};
    }

    function initConfig() {
        FlickrAssistant.Config.init(config || {});
    }

    function initContext() {
        _.extend(FlickrAssistant.Context, context);
    }

    function dataloader() {


    }

    this.bootstrap = function () {
        initConfig();
        initContext();
        initTemplates();
        initLayout();
        return this;
    }

    this.run = function () {
        layout.render();

        dataloader();

        return this;
    }

};


//
// MODELS
//


FlickrAssistant.Models.Dashboard = FlickrAssistant.Model.extend({
    "urlRoot": "api/dashboard",
    "idAttribute": "nsid"
});


FlickrAssistant.Models.UserInfo = FlickrAssistant.Model.extend({
    "urlRoot": "api/user/info",
    "idAttribute": "nsid"
});

FlickrAssistant.Collections.UserInfo = FlickrAssistant.Collection.extend({
    "url": "api/user/info",
    "model": FlickrAssistant.Models.UserInfo
});

FlickrAssistant.Models.StatsFavTag = FlickrAssistant.ModelReadOnly.extend({
    "idAttribute": "tag",
    initialize: function (attributes, options) {
        this.set("topphotos", this.get("photos").slice(0, Math.min(5, this.get("photos").length)))
    }
});

FlickrAssistant.Collections.StatsFavTag = FlickrAssistant.CollectionReadOnly.extend({
    "nsid": null,
    "initialize": function (models, options) {
        if (!_.has(options, "nsid")) {
            throw new FlickrAssistant.Error("You have to pass `nsid` to create instance of FlickrAssistant.Collections.StatsFavTag.");
        }
        this.nsid = options.nsid;
    },
    "url": function() {
        return "/api/stats/favs/" + this.nsid + "/tags";
    },
    "model": FlickrAssistant.Models.StatsFavTag,
    "comparator": function(a, b) {
          var ac = parseInt(a.get("count"));
          var bc = parseInt(b.get("count"));
          if (ac == bc) return 0;
          if (ac > bc) return -1;
          return 1;
    }
});

FlickrAssistant.Models.StatsFavOwner = FlickrAssistant.ModelReadOnly.extend({
    "idAttribute": "owner",
    initialize: function (attributes, options) {
          this.set("topphotos", this.get("photos").slice(0, Math.min(5, this.get("photos").length)))
    }
});

FlickrAssistant.Collections.StatsFavOwner = FlickrAssistant.CollectionReadOnly.extend({
    "nsid": null,
    "initialize": function (models, options) {
        if (!_.has(options, "nsid")) {
            throw new FlickrAssistant.Error("You have to pass `nsid` to create instance of FlickrAssistant.Collections.StatsFavOwner.");
        }
        this.nsid = options.nsid;
    },
    "url": function() {
        return "/api/stats/favs/" + this.nsid + "/owners";
    },
   "model": FlickrAssistant.Models.StatsFavOwner,
   "comparator": function(a, b) {
        var ac = parseInt(a.get("count"));
        var bc = parseInt(b.get("count"));
        if (ac == bc) return 0;
        if (ac > bc) return -1;
        return 1;
   }
});

//
// VIEWS
//


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
        this.setView(".left-panel", new FlickrAssistant.Views.TopFavedAuthors(), true)
        this.setView(".right-panel", new FlickrAssistant.Views.TopFavedTags(), true)
    }
});

FlickrAssistant.Views.Header = FlickrAssistant.BaseView.extend({
    template: "header",
    userInfo: null,
    dashboard: null,
    initialize: function () {
        this.userInfo = new FlickrAssistant.Models.UserInfo({"nsid": FlickrAssistant.Context.nsid});
        this.userInfo.fetch({async: false});

//        this.dashboard = new FlickrAssistant.Models.Dashboard({"nsid": FlickrAssistant.Context.nsid});
//        this.userInfo.fetch({async: false});
    },
    serialize: function () {
        return {
            user: this.userInfo.toJSON()
        };
    }
});

FlickrAssistant.Views.Layout = FlickrAssistant.BaseView.extend({
    initialize: function(options) {
        if (_.has(options, "header")) {
            this.setView("#header", options.header, true)
        }
        if (_.has(options, "content")) {
            this.setView("#main", options.content, true)
        }
    },
    template: "layout"
});
