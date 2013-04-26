// Hackernews

Widget("hackernews", {
  timer: null,
  init: function (node) {
    return this.update(node);
  },
  rate: 60000,
  update: function (node) {

    var self = this;

    this.timer && clearTimeout(this.timer);

    return $.getJSON("/hackernews/stream").then(function (json) {

      function render () {

        var head = json[Math.random() * json.length | 0];
        node.find(".user_img").empty().append($("<img>").attr("src", head.user.profile_image_url));
        node.find(".tweet").text(head.text);

        self.timer = setTimeout(function () {
          render();
        }, 5000);

      }

      render();

    });
  }
});
