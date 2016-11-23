FlickrAssistant.Components.TopPhotos = {
    decorate: function(coll, count = 3) {
        var l = coll.length, i = 0, j = 0, k = 0, id = "";
        var displayed = [];

        for (i = 0; i<l; i++) {
            j = 0; k = coll[i]["photos"].length;
            coll[i].topphotos = [];
            while (coll[i].topphotos.length < count && j < k) {
                id = coll[i]["photos"][j]["id"]
                if (-1 === displayed.indexOf(id)) {
                    coll[i].topphotos.push(coll[i]["photos"][j]);
                    displayed.push(id);
                }
                ++j;
            }
        }

        return coll;
    }
};