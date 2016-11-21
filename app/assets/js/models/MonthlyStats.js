FlickrAssistant.Models.MonthlyStats = FlickrAssistant.ModelReadOnly.extend({
    "idAttribute": "month"
});
FlickrAssistant.Collections.MonthlyStats = FlickrAssistant.CollectionReadOnly.extend({
    "nsid": null,
    "initialize": function (models, options) {
        if (!_.has(options, "nsid")) {
            throw new FlickrAssistant.Error("You have to pass `nsid` to create instance of FlickrAssistant.Collections.MonthlyStats.");
        }
        this.nsid = options.nsid;
    },
    "url": function() {
        return "/api/stats/user/" + this.nsid + "/monthly";
    },
   "model": FlickrAssistant.Models.MonthlyStats,
   "months": function() {
        var months = []; for (i=1;i<=9;i++) months.push("0" + i); months.push("10"); months.push("11"); months.push("12");
        return months;
   },
   "emptyMonth": function(y, m) { return {month: y + "-" + m, uploaded: 0, faved: 0, got_favs: 0}},
   "dataByYear": function() {
        var that = this;
        var js = this.toJSON();
        var years = []; var collection = []; var data = {};
        var first = "", last = "", ys = "";
        js.forEach(function(item) {
            var y = item.month.substr(0, 4)
            var m = item.month.substr(5, 2)
            if (first === "" ||  first > y) first = y;
            if (last === "" ||  last < y) last = y;
            if (!_.has(years, y)) years[y] = {};
            years[y][m] = item;
        });
        for (y=parseInt(first); y<=parseInt(last); y++) {
            ys = y.toString();
            if (!_.has(years, ys)) years[ys] = {};
            this.months().forEach(function(m) {
                if(!_.has(years[ys], m)) years[ys][m] = that.emptyMonth(y, m);
            });
        }
        for (y=parseInt(last);y>=parseInt(first);y--) {
            ys = y.toString();
            data = {"year": ys, months: []};
            this.months().forEach(function(m){ data.months.push(years[ys][m]); });
            collection.push(data);
        }

        return collection;
   },
   "comparator": function(a, b) {
        var am = a.get("month");
        var bm = b.get("month");
        if (am == bm) return 0;
        if (am > bm) return -1;
        return 1;
   }
});