

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
FlickrAssistant.Components = {};

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

FlickrAssistant.composeViews = function() {
    var args = Array.prototype.slice.call(arguments)
    return args.reduce(function(a, b) {
        var c = _.extend(a, b);
        var events = _.extend(a.events || {}, b.events || {})
        c.events = events
        return c
    }, {})
}

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
