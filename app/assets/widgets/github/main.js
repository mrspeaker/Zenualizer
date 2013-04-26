// Github

Widget("github", {
  init: function (node) {
    return this.update(node);
  },
  rate: 60000 * 5,
  update: function (node) {

    return $.getJSON("/github/eventStream").then(function (json) {
      //node.find(".tweeter").text("here");
      node.find(".commits").empty();
      json.slice(0, 12).forEach(function (c) {
      	console.log(c);
      	$("<li></li>").text(c.actor.login + ": " + c.payload.commits[0].message).appendTo(node.find(".commits"));
      });

    });
  }
});
