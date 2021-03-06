

Widget("france", {
  cloudConf: { x: 31, y: 21, z: 6, w: 3, h: 3, imgH: 256, imgW: 256 },
  franceCrop: { x: 20, y: 90, w: 613, h: 620 },
  getCloud: function (z, x, y) {
    var d = Q.defer();
    var img = new Image();
    img.onload = function () {
      d.resolve(img);
    }
    img.onerror = function (e) {
      d.reject(e);
    }
    img.src = "/map/clouds/"+z+"/"+x+"/"+y+".png";
    return d.promise;
  },
  init: function (node) {
    this.update(node);
  },
  smoothstep: function (edge0, edge1, x) {
    x = (x - edge0)/(edge1 - edge0); 
    return x;
  },
  update: function (node) {
    var self = this;
    var maybeImages = [];
    var positions = [];
    for (var x=0; x<this.cloudConf.w; ++x) {
      var xmap = x+this.cloudConf.x;
      for (var y=0; y<this.cloudConf.h; ++y) {
        var ymap = y+this.cloudConf.y;
        maybeImages.push(this.getCloud(this.cloudConf.z, xmap, ymap));
        positions.push({ x: x, y: y });
      }
    }
    return Q.all(maybeImages).then(function (images) {
      var map = document.createElement("canvas");
      map.width = self.cloudConf.imgW*self.cloudConf.w;
      map.height = self.cloudConf.imgH*self.cloudConf.h;
      var mapctx = map.getContext("2d");
      _.each(images, function (img, i) {
        var p = positions[i];
        mapctx.drawImage(img, p.x*self.cloudConf.imgW, p.y*self.cloudConf.imgH);
      });
      var cloud = document.createElement("canvas");
      cloud.width = self.franceCrop.w;
      cloud.height = self.franceCrop.h;
      var ctx = cloud.getContext("2d");
      var data = mapctx.getImageData(self.franceCrop.x, self.franceCrop.y, self.franceCrop.w, self.franceCrop.h).data;
      var output = ctx.createImageData(cloud.width, cloud.height);
      var w = cloud.width, h = cloud.height;
      var B = 30;
      function border (x, y) {
        return  Math.min(1, x/B)*
                Math.min(1, y/B)*
                Math.min(1, (w-x)/B)*
                Math.min(1, (h-y)/B);
      }

      for (var y = 0; y < h; y += 1) {
       for (var x = 0; x < w; x += 1) {
         var i = (y*w + x)*4;
         var intensity = data[i+3];
         output.data[i] = 255;
         output.data[i+1] = 180;
         output.data[i+2] = 100;
         output.data[i+3] = border(x, y)*0.8*self.smoothstep(-0.5, 1.0, intensity);
       }
      }
      ctx.putImageData(output, 0, 0);

      var $map = node.find(".map");
      $map.empty();
      $map.append(cloud);
    });
  }
});
