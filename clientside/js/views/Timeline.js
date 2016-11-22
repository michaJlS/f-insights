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
