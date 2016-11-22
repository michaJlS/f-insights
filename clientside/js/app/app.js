//Backbone.Layout.configure({ manage: true });
//var layout = new Backbone.Layout({ template: "#layout" });
Backbone.Layout.configure({});

Date.prototype.dmy = function() {
  var mm = this.getMonth() + 1;
  var dd = this.getDate();
  if (mm<10) mm = "0" + mm;
  if (dd<10) dd = "0" + dd;

  return [dd, mm, this.getFullYear()].join('.'); // padding
};

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

Handlebars.registerHelper("attr", function(val) {
    return val.replace('"', '&quot;')
});

Handlebars.registerHelper("alternate", function(i, what) {
    return what[i % what.length];
});

Handlebars.registerHelper("some", function() {
    for (var i = 0;i<arguments.length;i++) {
        if ("" !== arguments[i]) return arguments[i];
    }
    return "";
});

Handlebars.registerHelper("daterange", function(from, to) {
    var df = (new Date(from * 1000)).dmy(), dt = new Date(to * 1000).dmy();

    if (df == dt) return df;
    else return df + " - " + dt
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
            content: new FlickrAssistant.Views.Home(),
            menu: new FlickrAssistant.Views.Nav()
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
