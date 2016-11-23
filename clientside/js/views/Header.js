FlickrAssistant.Views.Header = FlickrAssistant.BaseView.extend({
    template: "header",
    userInfo: null,
    dashboard: null,
    events:  {
        "click .update-dashboard": "updateDashboard"
    },
    initialize: function () {
        this.userInfo = new FlickrAssistant.Models.UserInfo({"nsid": FlickrAssistant.Context.nsid});
        this.dashboard = new FlickrAssistant.Models.Dashboard({"nsid": FlickrAssistant.Context.nsid});
        this.userInfo.fetch();
        this.dashboard.fetchWithFallback();
    },
    updateDashboard: function () {
        var updatedDashboard = new FlickrAssistant.Models.Dashboard({"nsid": FlickrAssistant.Context.nsid})
        updatedDashboard.createNew({success: function(){
            location.reload();
        }});
        return false;
    },
    serialize: function () {
        return {
            user: this.userInfo.toJSON()
        };
    }
});
