// Zendaily

Widget("zendaily", {
  init: function (node) {
    return this.update(node);
  },
  rate: 5000,
  update: function (node) {
    return $.getJSON("/weather").then(function (json) {
      node.find(".temp").text(Math.round(json.temp));
      node.find(".descr").text(json.description);
    });
  }
});
