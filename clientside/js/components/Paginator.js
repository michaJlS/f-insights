FlickrAssistant.Components.Paginator = function() {

    this.opts = function (o) {
        return _.extend({perpage: 10}, o);
    };
    this.zero = function (coll, o) {
        return {
            "curr": 1,
            "from": 0,
            "to": o.perpage,
            "hasnext": o.perpage < coll.length
        };
    };
    this.nextPage = function (coll, o, state) {
        var next = null;
        if (!state.hasnext) {
            return false;
        }
        next = {
            "curr": state.curr + 1,
            "from": state.from + o.perpage,
            "to": state.to + o.perpage,
            "hasnext": false
        };
        next.hasnext = next.to < coll.length;
        return next;
    };
    this.getData = function (coll, state) {
        return coll.slice(state.from, state.to);
    };

    function from(page, o) { return o.perpage * (page-1); };
    function to(page, o) { return o.perpage * page; }

};

FlickrAssistant.Components.StatefulPaginator = function(data, options) {
    var pag = new FlickrAssistant.Components.Paginator()
    var opts = pag.opts(options)
    var d = null
    var state = null

    this.reset = function(data) {
        d = data
        state = pag.zero(d, opts)
    }

    this.getData = function() {
        return pag.getData(d, state)
    }

    this.nextPage = function() {
        var newState = pag.nextPage(d, opts, state)
        if (newState === false) {
            return false
        }
        state = newState
        return true
    }

    this.reset(data)
};