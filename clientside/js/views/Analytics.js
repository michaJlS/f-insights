FlickrAssistant.Views.Analytics = FlickrAssistant.BaseView.extend({
    template: "analytics",
    relativesData: [],
    relatives: null,
    chart: null,
    initialize: function () {
        this.relatives = new FlickrAssistant.Collections.Relative(null, {"user": FlickrAssistant.Context.nsid});
        this.relatives.fetch({
            success: this.loadedData.bind(this)
        });
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
        var colour = function (r) {
            if (r.faving == 0 && (r.faved > 0 || r.followed))
                return "#ff0000"
            if (r.faving > 0 && (r.faved > 0 || r.followed))
                return "#f1c40f"

            return "#0000ff"
        }

        var n = function(r) {
            if ("" !== r.realname) return r.realname
            if ("" !== r.username) return r.username
            return r.nsid
        }
        var u = function (r) { return "https://www.flickr.com/photos/" + r.nsid + "/" }
        var toPoint = function (r) {
            return [
                r.contacts,
                r.avg_points,
                'point {fill-color: ' + colour(r) +'; size: 2;}',
                '<a href="'+ u(r) +'" target="_blank">' + n(r) + '</a>'
            ];
        }

        var data = this.relativesData.map(toPoint)
        data.unshift(["Contacts", "Points (avg)", {'type': 'string', 'role': 'style'}, {'type': 'string', 'role': 'tooltip', 'p': {'html': true} }])

        this.chart = new google.visualization.ScatterChart(document.getElementById('relativeschart'));
        this.chart.draw(google.visualization.arrayToDataTable(data), {
            'width': 1200,
            'height': 500,
            tooltip: {isHtml: true, trigger: 'selection'},
            vAxis: {
                scaleType: 'log'
            },
            hAxis: {
                scaleType: 'log'
            }
        });
    },
    serialize: function() {
        return {};
    }
});
