FlickrAssistant.Views.SimpleList = FlickrAssistant.BaseView.extend({
     template: null,
     items: [],
     initialize: function (options) {
        this.template = options.tpl
        this.items = options.items
     },
     serialize: function () {
         return {
             items: this.items
         }
     }
});