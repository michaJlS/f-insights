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
