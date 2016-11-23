FlickrAssistant.Config = {

    options: {},
    defaults: {},

    setOption: function (key, val) {
        this.options[key] = val;
    },

    getOption: function (key) {
        if (!_.has(this.options, key)) {
            FlickrAssistant.debug("Undefined config option: " + key);
            return null;
        }
        return this.options[key];
    },

    init: function (vals) {
        _.extend(this.options, this.defaults);
        _.extend(this.options, vals);
    }

};
