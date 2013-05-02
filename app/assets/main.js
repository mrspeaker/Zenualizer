(function () {

  function checkForNewVersion () {
    Q.when($.getJSON("/version")).then(function (version) {
      if (CURRENT_VERSION != version) {
        console.log("new version! reloading...");
        location.reload();
      }
    });
  }
  setInterval(checkForNewVersion, 10000);

  window.Widget = function (id, widget) {
    var node = $("#"+id);
    Q.when(widget.init(node)).done(function () {
      console.log("widget "+id+" init.");
      node.addClass("visible");
    });
    widget.rate = widget.rate || 60000;
    if (widget.update) {
      var update = _.wrap(_.bind(widget.update, widget, node), function (update) {
        Q.when(update).done(function () {
          console.log("widget "+id+" updated.");
        });
      });
      setInterval(update, widget.rate);
    }
  }

}());
