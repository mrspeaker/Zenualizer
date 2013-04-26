(function () {

  function checkForNewVersion () {
    $.getJSON("/version").then(function (version) {
      if (CURRENT_VERSION != version) {
        console.log("new version! reloading...");
        location.reload();
      }
    });
  }
  setInterval(checkForNewVersion, 60000);

  window.Widget = function (id, widget) {
    var node = $("#"+id);
    $.when(widget.init(node)).then(function () {
      node.addClass("visible");
    });
    widget.rate = widget.rate || 60000;
    if (widget.update) {
      var update = _.bind(widget.update, widget, node);
      setInterval(update, widget.rate);
    }
  }

}());
