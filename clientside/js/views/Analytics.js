FlickrAssistant.Views.Analytics = FlickrAssistant.BaseView.extend({
    template: "analytics",
    relativesData: [],
    events: {
        "click .size-selector a": "resizePoints"
    },
    relatives: null,
    chart: null,
    styles: {
        ifaved_color: "#ff0000",
        mutual_color: "#f1c40f",
        gotfav_color: "#0000ff",
        size_by: "mutual"
    },
    initialize: function () {
        this.relatives = new FlickrAssistant.Collections.Relative(null, {"user": FlickrAssistant.Context.nsid});
        this.relatives.fetch({
            success: this.loadedData.bind(this)
        });
    },
    resizePoints: function(e) {
        var sizeBy = this.$(e.currentTarget).data("sizeby")
        e.stopImmediatePropagation()
        if (this.styles.size_by === sizeBy) {
            return false;
        }
        this.styles.size_by = sizeBy
        this.$('.size-selector a').removeClass('active')
        this.$(e.currentTarget).addClass('active')
        this.afterRender()
        return false;
    },
    loadedData: function () {
        this.relativesData = this.relatives.toJSON()
        this.render()
    },
    afterRender: function () {
        if (this.relativesData.length == 0) {
            return;
        }
        this.drawChart()
    },
    drawChart: function() {
        var that = this
        var colour = function (r) {
            if (r.faving == 0 && (r.faved > 0 || r.followed))
                return that.styles.ifaved_color
            if (r.faving > 0 && (r.faved > 0 || r.followed))
                return that.styles.mutual_color

            return that.styles.gotfav_color
        }

        var n = function(r) {
            if ("" !== r.realname) return r.realname
            if ("" !== r.username) return r.username
            return r.nsid
        }
        var u = function (r) { return "https://www.flickr.com/photos/" + r.nsid + "/" }
        var pointSize = function (r) {
            switch (that.styles.size_by) {
                case "ifaved":
                    return Math.round(Math.max(Math.min((r.faved) / 2, 8), 2))
                case "gotfav":
                    return Math.round(Math.max(Math.min((r.faving) / 1.5, 8), 2))
                default:
                    return Math.round(Math.max(Math.min((r.faving + r.faved) / 2, 8), 2))
            }
        }
        var toPoint = function (r) {
            return [
                r.contacts,
                r.avg_points,
                'point {fill-color: ' + colour(r) +'; size: ' + pointSize(r) + ';}',
                '<a href="'+ u(r) +'" target="_blank">' + n(r) + '</a>'
            ];
        }

        var data = this.relativesData.map(toPoint)
        data.unshift(["Contacts", "Points (avg)", {'type': 'string', 'role': 'style'}, {'type': 'string', 'role': 'tooltip', 'p': {'html': true} }])

        this.chart = new google.visualization.ScatterChart(document.getElementById('relativeschart'));
        this.chart.draw(google.visualization.arrayToDataTable(data), {
            width: 1200,
            height: 500,
            dataOpacity: 0.75,
            legend: 'none',
            tooltip: {isHtml: true, trigger: 'selection'},
            vAxis: {
                scaleType: 'log',
                title: 'Avg points'
            },
            hAxis: {
                scaleType: 'log',
                title: 'Following'
            }
        });
    },
    serialize: function() {
        return this.styles;
    }
});
