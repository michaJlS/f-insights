this["FlickrAssistantTemplates"] = this["FlickrAssistantTemplates"] || {};

this["FlickrAssistantTemplates"]["header"] = Handlebars.template({"compiler":[7,">= 4.0.0"],"main":function(container,depth0,helpers,partials,data) {
    var stack1, alias1=container.lambda, alias2=container.escapeExpression;

  return "<div class=\"inside\">\n    <h1>the assistant</h1>\n    <div class=\"profile\">\n        Hi "
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.fullname : stack1), depth0))
    + "\n        <img src=\"http://flickr.com/buddyicons/"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.nsid : stack1), depth0))
    + ".jpg\">\n        <a href=\"#\" class=\"logout\">[Logout]</a>\n    </div>\n</div>\n";
},"useData":true});

this["FlickrAssistantTemplates"]["home"] = Handlebars.template({"compiler":[7,">= 4.0.0"],"main":function(container,depth0,helpers,partials,data) {
    return "<div class=\"row\">\n    <div class=\"col-md-6 left-panel\"></div>\n    <div class=\"col-md-6 right-panel\"></div>\n</div>";
},"useData":true});

this["FlickrAssistantTemplates"]["layout"] = Handlebars.template({"compiler":[7,">= 4.0.0"],"main":function(container,depth0,helpers,partials,data) {
    return "<header id=\"header\"></header>\n<section id=\"main\" class=\"container\"></section>\n<footer id=\"footer\"></footer>";
},"useData":true});

this["FlickrAssistantTemplates"]["top-faved-authors"] = Handlebars.template({"1":function(container,depth0,helpers,partials,data) {
    var stack1, helper, alias1=depth0 != null ? depth0 : {}, alias2=helpers.helperMissing, alias3="function", alias4=container.escapeExpression;

  return "        <tr>\n            <td><a href=\"https://www.flickr.com/photos/"
    + alias4(((helper = (helper = helpers.owner || (depth0 != null ? depth0.owner : depth0)) != null ? helper : alias2),(typeof helper === alias3 ? helper.call(alias1,{"name":"owner","hash":{},"data":data}) : helper)))
    + "/\" target=\"_blank\"><img src=\"http://flickr.com/buddyicons/"
    + alias4(((helper = (helper = helpers.owner || (depth0 != null ? depth0.owner : depth0)) != null ? helper : alias2),(typeof helper === alias3 ? helper.call(alias1,{"name":"owner","hash":{},"data":data}) : helper)))
    + ".jpg\"> "
    + alias4(((helper = (helper = helpers.owner_name || (depth0 != null ? depth0.owner_name : depth0)) != null ? helper : alias2),(typeof helper === alias3 ? helper.call(alias1,{"name":"owner_name","hash":{},"data":data}) : helper)))
    + "</a></td>\n            <td>"
    + alias4(((helper = (helper = helpers.count || (depth0 != null ? depth0.count : depth0)) != null ? helper : alias2),(typeof helper === alias3 ? helper.call(alias1,{"name":"count","hash":{},"data":data}) : helper)))
    + "</td>\n        </tr>\n        <tr>\n            <td colspan=\"2\">\n"
    + ((stack1 = helpers.each.call(alias1,(depth0 != null ? depth0.topphotos : depth0),{"name":"each","hash":{},"fn":container.program(2, data, 0),"inverse":container.noop,"data":data})) != null ? stack1 : "")
    + "            </td>\n        </tr>\n";
},"2":function(container,depth0,helpers,partials,data) {
    var stack1, alias1=container.lambda, alias2=container.escapeExpression;

  return "                <a href=\"https://www.flickr.com/photos/"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.photo : depth0)) != null ? stack1.owner : stack1), depth0))
    + "/"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.photo : depth0)) != null ? stack1.id : stack1), depth0))
    + "\" target=\"_blank\"><img src=\""
    + alias2(alias1(((stack1 = ((stack1 = (depth0 != null ? depth0.photo : depth0)) != null ? stack1.urls : stack1)) != null ? stack1.largeSquareThumb : stack1), depth0))
    + "\"></a>\n";
},"compiler":[7,">= 4.0.0"],"main":function(container,depth0,helpers,partials,data) {
    var stack1;

  return "<h2>Most faved authors</h2>\n<table class=\"table table-striped\">\n    <thead>\n    <tr>\n        <th>Author</th>\n        <th>Count</th>\n    </tr>\n    </thead>\n    <tbody>\n"
    + ((stack1 = helpers.each.call(depth0 != null ? depth0 : {},(depth0 != null ? depth0.owners : depth0),{"name":"each","hash":{},"fn":container.program(1, data, 0),"inverse":container.noop,"data":data})) != null ? stack1 : "")
    + "    </tbody>\n</table>";
},"useData":true});

this["FlickrAssistantTemplates"]["top-faved-tags"] = Handlebars.template({"1":function(container,depth0,helpers,partials,data) {
    var stack1, helper, alias1=depth0 != null ? depth0 : {}, alias2=helpers.helperMissing, alias3="function", alias4=container.escapeExpression;

  return "        <tr>\n            <td>"
    + alias4(((helper = (helper = helpers.tag || (depth0 != null ? depth0.tag : depth0)) != null ? helper : alias2),(typeof helper === alias3 ? helper.call(alias1,{"name":"tag","hash":{},"data":data}) : helper)))
    + "</td>\n            <td>"
    + alias4(((helper = (helper = helpers.count || (depth0 != null ? depth0.count : depth0)) != null ? helper : alias2),(typeof helper === alias3 ? helper.call(alias1,{"name":"count","hash":{},"data":data}) : helper)))
    + "</td>\n        </tr>\n        <tr>\n            <td colspan=\"2\">\n"
    + ((stack1 = helpers.each.call(alias1,(depth0 != null ? depth0.topphotos : depth0),{"name":"each","hash":{},"fn":container.program(2, data, 0),"inverse":container.noop,"data":data})) != null ? stack1 : "")
    + "            </td>\n        </tr>\n";
},"2":function(container,depth0,helpers,partials,data) {
    var stack1, alias1=container.lambda, alias2=container.escapeExpression;

  return "                    <a href=\"https://www.flickr.com/photos/"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.photo : depth0)) != null ? stack1.owner : stack1), depth0))
    + "/"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.photo : depth0)) != null ? stack1.id : stack1), depth0))
    + "\" target=\"_blank\"><img src=\""
    + alias2(alias1(((stack1 = ((stack1 = (depth0 != null ? depth0.photo : depth0)) != null ? stack1.urls : stack1)) != null ? stack1.largeSquareThumb : stack1), depth0))
    + "\"></a>\n";
},"compiler":[7,">= 4.0.0"],"main":function(container,depth0,helpers,partials,data) {
    var stack1;

  return "<h2>Most faved tags</h2>\n<table class=\"table table-striped\">\n    <thead>\n    <tr>\n        <th>Tag</th>\n        <th>Count</th>\n    </tr>\n    </thead>\n    <tbody>\n"
    + ((stack1 = helpers.each.call(depth0 != null ? depth0 : {},(depth0 != null ? depth0.tags : depth0),{"name":"each","hash":{},"fn":container.program(1, data, 0),"inverse":container.noop,"data":data})) != null ? stack1 : "")
    + "    </tbody>\n</table>";
},"useData":true});