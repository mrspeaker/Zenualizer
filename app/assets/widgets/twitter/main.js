// Twitter

Widget("twitter", {
  init: function (node) {
    return this.update(node);
  },
  rate: 60000,
  update: function (node) {
    return $.getJSON("/timelineAll").then(function (json) {
      function render () {
        var head = json[Math.random() * json.length | 0];
        node.find(".tweeter").text(head.user.screen_name);
        node.find(".tweet").text(head.text);
        setTimeout(function () {
          render();
        }, 5000);
      }

      render();

    });
  }
});
