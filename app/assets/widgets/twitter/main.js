// Twitter

Widget("twitter", {
  init: function (node) {
  	return this.update(node);
  },
  rate: 60000,
  update: function (node) {
    return $.getJSON("/timelineAll").then(function (json) {
    	var head = json[Math.random() * json.length | 0];
      node.find(".temp").text(head.user.screen_name);
      node.find(".descr").text(head.text);
    });
  }
});
