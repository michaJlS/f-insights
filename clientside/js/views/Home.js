FlickrAssistant.Views.Home = FlickrAssistant.BaseView.extend({
    template: "home",
    initialize: function () {
        this.setView(".faved-filters", new FlickrAssistant.Views.DateRange({
            filterChange: this.onFilterChange.bind(this)
        }), true)
        this.setView(".faved-authors", new FlickrAssistant.Views.TopFavedAuthors(), true)
        this.setView(".faved-tags", new FlickrAssistant.Views.TopFavedTags(), true)
        this.setView("#timeline", new FlickrAssistant.Views.Timeline(), true)
        this.setView(".faving-users", new FlickrAssistant.Views.FavingUsers(), true)
        this.setView(".my-hot-tags", new FlickrAssistant.Views.MyHotTags(), true)
    },
    onFilterChange: function(from, to) {
        ['.faved-authors', '.faved-tags'].map(this.getView.bind(this)).forEach(function (vw) { vw.dateRangeChanged(from, to); });
    }
});
