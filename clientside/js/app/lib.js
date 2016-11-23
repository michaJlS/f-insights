
Date.prototype.dmy = function() {
  var mm = this.getMonth() + 1;
  var dd = this.getDate();
  if (mm<10) mm = "0" + mm;
  if (dd<10) dd = "0" + dd;

  return [dd, mm, this.getFullYear()].join('.'); // padding
};
