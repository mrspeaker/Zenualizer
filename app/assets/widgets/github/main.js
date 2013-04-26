// Github

Widget("github", {
	timer: null,
  init: function (node) {
    return this.update(node);
  },
  rate: 60000 * 5,
  update: function (node) {

    return $.getJSON("/github/eventStream").then(function (json) {

      var self = this;

    	this.timer && clearTimeout(this.timer);

			node.find(".commits").empty();
      function print (commits) {

      	var c = commits[0];
      	if (c.payload.commits) {
      		$("<li></li>")
      			.prependTo(node.find(".commits"))
      			.html("<span class='actor'>" + c.actor.login + "</span>: " + c.payload.commits[0].message);
      	}

				node.find(".commits li:gt(14)").remove();

      	self.timer = setTimeout(function () {
      		print(commits.length > 1 ? commits.slice(1) : json)
      	}, (Math.random() * 1000 | 0) + 300);

      }
      print(json);

    });
  }
});
