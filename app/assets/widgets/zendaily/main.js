// Zendaily

Widget("zendaily", {
  init: function (node) {
    return this.update(node);
  },
  rate: 60000 * 5,
  update: function (node) {
    return Q.when($.getJSON("/zendaily/stream")).then(function (json) {

      node.find(".stream").empty();

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
