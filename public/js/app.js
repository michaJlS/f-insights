//Backbone.Layout.configure({ manage: true });
//var layout = new Backbone.Layout({ template: "#layout" });
Backbone.Layout.configure({});

var FlickrAssistant = {};
FlickrAssistant.Models = {};
FlickrAssistant.Collections = {};
FlickrAssistant.Templates = {};
FlickrAssistant.Context = {
    nsid: "",
    s: "",
    t: ""
};

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
            args[2]["headers"]["fa_secret"] = FlickrAssistant.Context.s;
            args[2]["headers"]["fa_token"] = FlickrAssistant.Context.t;
        } else {
            FlickrAssistant.debug("FlickrAssistant.Persist: args has no el 2!");
        }
        return Backbone.sync.apply(obj, args);
    }

};

FlickrAssistant.Layout = FlickrAssistant.BaseView.extend({ template: "layout" });

FlickrAssistant.Collection = Backbone.Collection.extend({
    sync: function() {
        return FlickrAssistant.Persist.sync(this, arguments);
    }
});

FlickrAssistant.Model = Backbone.Model.extend({
    sync: function() {
        return FlickrAssistant.Persist.sync(this, arguments);
    }
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
        layout = new FlickrAssistant.Layout({el: "#layout"});
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

    this.bootstrap = function () {
        initConfig();
        initContext();
        initTemplates();
        initLayout();
        return this;
    }

    this.run = function () {
        layout.render();
        return this;
    }

};


//
// MODELS
//

FlickrAssistant.Models.UserInfo = FlickrAssistant.Model.extend({
    "urlRoot": "api/user/info",
    "idAttribute": "nsid"
});

FlickrAssistant.Collections.UserInfo = FlickrAssistant.Collection.extend({
    "url": "api/user/info",
    "model": FlickrAssistant.Models.UserInfo
});

$(document).ready(function() {
    var fa = new FlickrAssistant.App();
    fa.bootstrap().run();
});
