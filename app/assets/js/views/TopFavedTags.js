FlickrAssistant.Views.TopFavedTags = FlickrAssistant.BaseView.extend({
    template: "top-faved-tags",
    tags: [],
    page: 0,
    d: false,
    displayedPics: [],
    events: {
        "click .moar": "onAddPage"
    },
    loadData: function (from, to) {
        var d = "" + from + to;
        if (d === this.d) {
            return;
        }
        this.d = d;
        this.displayedPics = [];
        this.page = 0;
        this.tags.reset();
        this.render();
        this.tags.fetch({
            success: this.addPage.bind(this),
            data: {threshold: 20, from_timestamp: from, to_timestamp: to}
        });

    },
    initialize: function () {
        this.tags = new FlickrAssistant.Collections.StatsFavTag(null, {"nsid": FlickrAssistant.Context.nsid});
        this.loadData("", "");
    },
    onAddPage: function(e) {
        e.stopImmediatePropagation();
        this.addPage();
        return false;
    },
    addPage: function() {
        var from = function(x){ return 10 * (x-1); },
            to = function(x) { return 10 * x; },
            tagsJSON = [], child = null;

        if (to(this.page) >= this.tags.length) {
            return false;
        }

        ++this.page;
        tagsJSON = this.tags.toJSON().slice(from(this.page), to(this.page));
        child = new FlickrAssistant.Views.FavedTagsList({
            tags: this.decorate(tagsJSON)
        });
        this.setView(".pages", child, true);
        child.render();
    },
    decorate: function(tagsJSON) {
        var l = tagsJSON.length, i = 0, j = 0, k = 0, id = "";

        for (i = 0; i<l; i++) {
            j = 0; k = tagsJSON[i]["photos"].length;
            tagsJSON[i].topphotos = [];
            while (tagsJSON[i].topphotos.length < 3 && j < k) {
                id = tagsJSON[i]["photos"][j]["photo"]["id"]
                if (-1 === this.displayedPics.indexOf(id)) {
                    tagsJSON[i].topphotos.push(tagsJSON[i]["photos"][j]);
                    this.displayedPics.push(id);
                }
                ++j;
            }
        }

        return tagsJSON;
    },
     dateRangeChanged: function(from, to) {
         this.loadData(from, to);
     }
});

FlickrAssistant.Views.FavedTagsList = FlickrAssistant.BaseView.extend({
     template: "faved-tags-list",
     tags: [],
     initialize: function (options) {
         this.tags = options.tags
     },
     serialize: function () {
         return {
             tags: this.tags
         }
     }
 });
