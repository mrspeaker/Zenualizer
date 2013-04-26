// Zendaily

Widget("zendaily", {
  init: function (node) {
    return this.update(node);
  },
  rate: 5000,
  update: function (node) {
    return $.getJSON("/zendaily/stream").then(function (json) {

      json.forEach(function (e) {

        console.log(e);
        $("<li></li>")
          .text(e.author + " presents... " + e.type + ": " + e.summary)
          .appendTo(node.find(".stream"))
      });

    });
  }
});
