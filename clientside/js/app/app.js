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
