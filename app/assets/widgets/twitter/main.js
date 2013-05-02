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
      var topTweets = _.take(json, 5);
      var i = 0;

      var $imgContainer = node.find(".user_imgs").empty();
      var userImages = _.map(topTweets, function (twitt) {
          return $('<img src="'+twitt.userprofile_image_url+'" />');
        });
      _.each(userImages, function (img) {
        $imgContainer.append(img);
      });

      function next () {
        var head = topTweets[i];

        if (head && head.text) {
          node.find(".tweeter").text(head.userscreen_name);
          node.find(".tweet").text(head.text);
          userImages[i].addClass("current").siblings().removeClass("current");
        }

        i = i<topTweets.length-1 ? i+1 : 0; // rotate
      }

      self.timer = setInterval(next, self.flipRate);
      next();

    });
  }
});
