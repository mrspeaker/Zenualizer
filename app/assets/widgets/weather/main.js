// Weather

Widget("weather", {
  init: function (node) {
    return this.update(node);
  },
  rate: 60000 * 1,
  update: function (node) {
    return $.getJSON("/weather").then(function (json) {
      node.find(".temp").text(Math.round(json.temp));
      node.find(".descr").text(json.description);
    });
  }
});
