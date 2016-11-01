this["FlickrAssistantTemplates"] = this["FlickrAssistantTemplates"] || {};

this["FlickrAssistantTemplates"]["faved-tags-list"] = Handlebars.template({"1":function(container,depth0,helpers,partials,data) {
    var stack1, helper, alias1=depth0 != null ? depth0 : {}, alias2=helpers.helperMissing, alias3="function", alias4=container.escapeExpression;

  return "        <h5>\n            <strong>"
    + alias4(((helper = (helper = helpers.tag || (depth0 != null ? depth0.tag : depth0)) != null ? helper : alias2),(typeof helper === alias3 ? helper.call(alias1,{"name":"tag","hash":{},"data":data}) : helper)))
    + "</strong>\n            <span class=\"count\">("
    + alias4(((helper = (helper = helpers.count || (depth0 != null ? depth0.count : depth0)) != null ? helper : alias2),(typeof helper === alias3 ? helper.call(alias1,{"name":"count","hash":{},"data":data}) : helper)))
    + ")</span>\n        </h5>\n        <p>\n"
    + ((stack1 = helpers.each.call(alias1,(depth0 != null ? depth0.topphotos : depth0),{"name":"each","hash":{},"fn":container.program(2, data, 0),"inverse":container.noop,"data":data})) != null ? stack1 : "")
    + "        </p>\n";
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

  return "<div>\n"
    + ((stack1 = helpers.each.call(depth0 != null ? depth0 : {},(depth0 != null ? depth0.tags : depth0),{"name":"each","hash":{},"fn":container.program(1, data, 0),"inverse":container.noop,"data":data})) != null ? stack1 : "")
    + "</div>";
},"useData":true});

this["FlickrAssistantTemplates"]["header"] = Handlebars.template({"compiler":[7,">= 4.0.0"],"main":function(container,depth0,helpers,partials,data) {
    var stack1, alias1=container.lambda, alias2=container.escapeExpression;

  return "<div class=\"inside\">\n    <h1>the assistant</h1>\n    <div class=\"profile\">\n        Hi "
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.fullname : stack1), depth0))
    + "\n        <img src=\"http://flickr.com/buddyicons/"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.nsid : stack1), depth0))
    + ".jpg\">\n        <a href=\"#\" class=\"logout\">[Logout]</a>\n    </div>\n</div>\n";
},"useData":true});

this["FlickrAssistantTemplates"]["home"] = Handlebars.template({"compiler":[7,">= 4.0.0"],"main":function(container,depth0,helpers,partials,data) {
    return "<div class=\"row\">\n    <div class=\"col-md-6 left-panel faved-tags\"></div>\n    <div class=\"col-md-6 right-panel faved-authors\"></div>\n</div>\n";
},"useData":true});

this["FlickrAssistantTemplates"]["layout"] = Handlebars.template({"compiler":[7,">= 4.0.0"],"main":function(container,depth0,helpers,partials,data) {
    return "<header id=\"header\"></header>\n<nav id=\"menu\"></nav>\n<section id=\"main\" class=\"container\"></section>\n<footer id=\"footer\"></footer>";
},"useData":true});

this["FlickrAssistantTemplates"]["nav"] = Handlebars.template({"compiler":[7,">= 4.0.0"],"main":function(container,depth0,helpers,partials,data) {
    return "<ul class=\"nav nav-pills\">\n    <li role=\"presentation\" class=\"active\"><a href=\"#\">Home</a></li>\n    <li role=\"presentation\"><a href=\"#\">Profile</a></li>\n    <li role=\"presentation\"><a href=\"#\">Messages</a></li>\n</ul>\n";
},"useData":true});

this["FlickrAssistantTemplates"]["top-faved-authors"] = Handlebars.template({"1":function(container,depth0,helpers,partials,data) {
    return "<a href=\"#\" class=\"remove-contacts-filter\">Remove filter</a>\n";
},"3":function(container,depth0,helpers,partials,data) {
    return "<a href=\"#\" class=\"non-contacts\">Show only non-contacts</a>\n";
},"5":function(container,depth0,helpers,partials,data) {
    var stack1, helper, alias1=depth0 != null ? depth0 : {}, alias2=helpers.helperMissing, alias3="function", alias4=container.escapeExpression;

  return "        <h5>\n           <a href=\"https://www.flickr.com/photos/"
    + alias4(((helper = (helper = helpers.owner || (depth0 != null ? depth0.owner : depth0)) != null ? helper : alias2),(typeof helper === alias3 ? helper.call(alias1,{"name":"owner","hash":{},"data":data}) : helper)))
    + "/\" target=\"_blank\"><img src=\"http://flickr.com/buddyicons/"
    + alias4(((helper = (helper = helpers.owner || (depth0 != null ? depth0.owner : depth0)) != null ? helper : alias2),(typeof helper === alias3 ? helper.call(alias1,{"name":"owner","hash":{},"data":data}) : helper)))
    + ".jpg\"> <strong>"
    + alias4(((helper = (helper = helpers.owner_name || (depth0 != null ? depth0.owner_name : depth0)) != null ? helper : alias2),(typeof helper === alias3 ? helper.call(alias1,{"name":"owner_name","hash":{},"data":data}) : helper)))
    + "</strong></a></td>\n           <span class=\"count\">("
    + alias4(((helper = (helper = helpers.count || (depth0 != null ? depth0.count : depth0)) != null ? helper : alias2),(typeof helper === alias3 ? helper.call(alias1,{"name":"count","hash":{},"data":data}) : helper)))
    + ")</span>\n        </h5>\n        <p>\n"
    + ((stack1 = helpers.each.call(alias1,(depth0 != null ? depth0.topphotos : depth0),{"name":"each","hash":{},"fn":container.program(6, data, 0),"inverse":container.noop,"data":data})) != null ? stack1 : "")
    + "       </p>\n";
},"6":function(container,depth0,helpers,partials,data) {
    var stack1, alias1=container.lambda, alias2=container.escapeExpression;

  return "            <a href=\"https://www.flickr.com/photos/"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.photo : depth0)) != null ? stack1.owner : stack1), depth0))
    + "/"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.photo : depth0)) != null ? stack1.id : stack1), depth0))
    + "\" target=\"_blank\"><img src=\""
    + alias2(alias1(((stack1 = ((stack1 = (depth0 != null ? depth0.photo : depth0)) != null ? stack1.urls : stack1)) != null ? stack1.largeSquareThumb : stack1), depth0))
    + "\"></a>\n";
},"compiler":[7,">= 4.0.0"],"main":function(container,depth0,helpers,partials,data) {
    var stack1, alias1=depth0 != null ? depth0 : {};

  return "<h2>Faved authors\n"
    + ((stack1 = helpers["if"].call(alias1,(depth0 != null ? depth0.nonContacts : depth0),{"name":"if","hash":{},"fn":container.program(1, data, 0),"inverse":container.program(3, data, 0),"data":data})) != null ? stack1 : "")
    + "</h2>\n\n<div>\n"
    + ((stack1 = helpers.each.call(alias1,(depth0 != null ? depth0.owners : depth0),{"name":"each","hash":{},"fn":container.program(5, data, 0),"inverse":container.noop,"data":data})) != null ? stack1 : "")
    + "</div>\n\n<table class=\"table table-striped\">\n    <thead>\n    <tr>\n        <th>Author</th>\n        <th>Count</th>\n    </tr>\n    </thead>\n    <tbody>\n    </tbody>\n</table>\n<p><a href=\"#\" class=\"moar\">Show more ...</a></p>";
},"useData":true});

this["FlickrAssistantTemplates"]["top-faved-tags"] = Handlebars.template({"compiler":[7,">= 4.0.0"],"main":function(container,depth0,helpers,partials,data) {
    return "<h2>Faved tags</h2>\n<div class=\"pages\"></div>\n<p><a href=\"#\" class=\"moar\">Show more ...</a></p>";
},"useData":true});