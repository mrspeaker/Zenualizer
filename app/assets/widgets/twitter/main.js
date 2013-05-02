// Twitter

Widget("twitter", {
  timer: null,
  init: function (node) {
    return this.update(node);
  },
  rate: 60000,
  flipRate: 8000,
  update: function (node) {

    var self = this;

    this.timer && clearTimeout(this.timer);

    return Q.when($.getJSON("/twitter/daily")).then(function (json) {
      function render () {

        var head = json[Math.random() * json.length | 0];

        if (head && head.text) {
          node.find(".tweeter").text(head.userscreen_name);
          node.find(".tweet").text(head.text);
          var userImg = node.find(".user_img img"),
            userImg2 = node.find(".user_img_2 img");

          node.find(".user_img_3").empty().append(userImg2);
          node.find(".user_img_2").empty().append(userImg);
          node.find(".user_img").empty().append($("<img>").attr("src", head.userprofile_image_url));
        }

        self.timer = setTimeout(function () {
          render();
        }, self.flipRate);

      }

      render();

    });
  }
});
