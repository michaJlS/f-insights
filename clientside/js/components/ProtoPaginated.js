FlickrAssistant.Components.ProtoPaginated = {
    paginator: null,
    initPaginator: function (perPage) {
        this.paginator = new FlickrAssistant.Components.StatefulPaginator([], {"perpage": perPage});
    },
    events: {
        "click .moar": "onIncPage"
    },
    onIncPage: function(e) {
        e.stopImmediatePropagation();
        this.incPage();
        return false;
    },
    incPage: function() {
        if (!this.paginator.nextPage()) {
            return false;
        }
        this.addPage();
    }
};