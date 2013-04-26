// Zendaily

Widget("zendaily", {
  init: function (node) {
    return this.update(node);
  },
  rate: 5000,
  update: function (node) {
    return $.getJSON("/zendaily/stream").then(function (json) {

      json.forEach(function (e) {

        var start = new Date(e.start);
        if (!start) {
          return;
        }

        $("<li></li>")
          .html("<span>" + start.getDate() + "/" + (start.getMonth() + 1) + "." + e.author + " presents:" + e.summary)
          .appendTo(node.find(".stream"))
      });

    });
  }
});
