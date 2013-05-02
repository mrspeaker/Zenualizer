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

    return Q.when($.getJSON("/hackernews/stream")).then(function (json) {

      function render () {

        var head = json[Math.random() * json.length | 0];

        if (head && head.user) {
          node.find(".user_img").empty().append($("<img>").attr("src", head.user.profile_image_url));
          node.find(".tweet").text(head.text.replace(/http.*/g, ""));
        }

        self.timer = setTimeout(function () {
          render();
        }, 5000);

      }

      render();

    });
  }
});
