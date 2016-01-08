//Backbone.Layout.configure({ manage: true });
//var layout = new Backbone.Layout({ template: "#layout" });
Backbone.Layout.configure({});

var FlickrAssistant = {};

FlickrAssistant.Templates = FlickrAssistantTemplates || {};

FlickrAssistant.BaseView = Backbone.Layout.extend({
    fetchTemplate: function (path) {
        return _.has(FlickrAssistant.Templates, path) ? FlickrAssistant.Templates[path] : function() { console.log("Undefined template: " + path); return ""; }
    }
});

FlickrAssistant.Layout = FlickrAssistant.BaseView.extend({
    el: "#layout",
    template: "layout"
});


FlickrAssistant.App = function() {

    var that = this;
    var layout = null

    function initLayout() {
        layout = new FlickrAssistant.Layout();
    }

    this.bootstrap = function () {
        initLayout();
        return this;
    }

    this.run = function () {
        layout.render();
        return this;
    }

}


$(document).ready(function() {
    var fa = new FlickrAssistant.App();
    fa.bootstrap().run();
});

