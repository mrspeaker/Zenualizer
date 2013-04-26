
// Weather
(function () {
  var node = $("#weather");
  $.getJSON("/weather").then(function (json) {
    node.find(".temp").text(Math.round(json.temp));
    node.find(".descr").text(json.description);
  });
}());
