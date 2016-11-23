Handlebars.registerHelper("debug", function(optionalValue) {
  console.log("Current Context");
  console.log("====================");
//  console.log(this);

  if (optionalValue) {
    console.log("Value");
    console.log("====================");
    console.log(optionalValue);
  }
});

Handlebars.registerHelper("attr", function(val) {
    return val.replace('"', '&quot;')
});

Handlebars.registerHelper("alternate", function(i, what) {
    return what[i % what.length];
});

Handlebars.registerHelper("some", function() {
    for (var i = 0;i<arguments.length;i++) {
        if ("" !== arguments[i]) return arguments[i];
    }
    return "";
});

Handlebars.registerHelper("daterange", function(from, to) {
    var df = (new Date(from * 1000)).dmy(), dt = new Date(to * 1000).dmy();

    if (df == dt) return df;
    else return df + " - " + dt
});

Handlebars.registerHelper("f", function(f) {
    return f.toFixed(2);
});

Handlebars.registerHelper("following", function(whom, contacts) {
   if (-1 === contacts.indexOf(whom)) {
    return "";
   }

   return new Handlebars.SafeString('<span class="glyphicon glyphicon-user" aria-hidden="true"></span>');
});
