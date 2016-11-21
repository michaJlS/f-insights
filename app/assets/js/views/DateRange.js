FlickrAssistant.Views.DateRange = FlickrAssistant.BaseView.extend({
    template: "daterange",
    filterChange: function(){},
    fid: 0,
    initialize: function(options) {
        this.filterChange = options.filterChange;
        this.fid = 'form-daterange-' + Math.random() * 10000 + '-' + Math.random() * 10000 ;
    },
    events: {
        "submit .daterange": "onSubmit"
    },
    serialize: function() {
        return {fid: this.fid};
    },
    isDate: function(x) { return x.match(/^20[0-9][0-9]-[01][0-9]-[0-3][0-9]$/); },
    ok: function(x) { return (x === "") || this.isDate(x); },
    f:  function(n){ return this.$('[name=' + n + ']'); },
    fv: function(n) {
            var f = this.f(n);
            var v = f.val().trim();
            var ok = this.ok(v);
            return {"f": f, "v": v, "ok": ok, "n": n };
        },
    feedback: function(x) {
        if (x.ok) {
            x.f.parent('div').removeClass('has-error');
            return;
        }
        x.f.parent('div').addClass('has-error');
    },
    ts: function(v) {
        var m = v.match(/(\d+)-0*(\d+)-0*(\d+)/);
        if ("" === v) {
            return v;
        }
        return Math.round((new Date(m[1], m[2], m[3])).getTime()/1000);
    },
    decorate: function(x) { x.d = this.ts(x.v) },
    onSubmit: function() {
        var data = ['from', 'to'].map(this.fv.bind(this));
        data.forEach(this.feedback.bind(this));
        if (!data.reduce(function(x, y){return {ok: x.ok && y.ok}; }, {ok: true}).ok) {
            return false;
        }
        data.forEach(this.decorate.bind(this));
        this.filterChange(data[0].d, data[1].d);

        return false;
    }
});