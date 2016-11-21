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
