// Weather

Widget("weather", {
  init: function (node) {
    return this.update(node);
  },
  rate: 60000 * 1,
  update: function (node) {
    return Q.when($.getJSON("/weather")).then(function (json) {
      node.find(".temp").text(Math.round(json.temp));
      node.find(".brief").text(json.brief);
      node.find(".descr").text(json.description);
    });
  }
});
