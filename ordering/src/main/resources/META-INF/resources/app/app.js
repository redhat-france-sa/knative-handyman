// global on load
global.listeners.uiconfig.push(
  async function (uiconfig) {
    console.log('custom operation on load ' + JSON.stringify(uiconfig));
  }
)

// function is executed when socket message arrives
function functionalProcessing(jsonMsg) {
  data = jsonMsg.data;
  type = jsonMsg.type;
  // do you custom functional stuff here
  switch (type) {
    case "upload":
      jsonMsg.formatting = {
        status: function (value) {
          if (value == "done") {
            return "positive";
          } else {
            return "negative";
          }
        }
      }
      break;
    case "blend-file-broadcast":
      console.log("functional processing for type " + type);
      jsonMsg.formatting = {
        status: function (value) {
          if (value == "received" || value == "open") {
            return "positive";
          } else {
            return "negative";
          }
        }
      }
      break;
    case "distributed-frame-render-broadcast":
      console.log("functional processing for type " + type);
      delete jsonMsg.data[0].areaX;
      delete jsonMsg.data[0].areaY;
      delete jsonMsg.data[0].resolutionX;
      delete jsonMsg.data[0].resolutionY;
      delete jsonMsg.data[0].samples;
      delete jsonMsg.data[0].frameDivider;
      delete jsonMsg.data[0].outputPrefix;
      delete jsonMsg.data[0].renderedFilePath;
      delete jsonMsg.data[0].totalTiles;

      jsonMsg.data[0].start = (new Date(jsonMsg.data[0].start)).toLocaleTimeString();
      jsonMsg.data[0].end = (new Date(jsonMsg.data[0].end)).toLocaleTimeString();
      jsonMsg.data[0].duration = forHumans(jsonMsg.data[0].duration);
      jsonMsg.formatting = {
        status: function (value) {
          if (value == "done") {
            return "positive";
          } else {
            return "negative";
          }
        }
      }
      break;
    case "tile":
      console.log("functional processing for type " + type);
      var c = document.getElementById("image");

      if (c.width != jsonMsg.data[0].resolutionX) {
        c.width = jsonMsg.data[0].resolutionX;
      }

      if (c.height != jsonMsg.data[0].resolutionY) {
        c.height = jsonMsg.data[0].resolutionY;
      }

      var ctx = c.getContext("2d");
      var img = new Image();
      console.log(jsonMsg);
      img.onload = function () {
        ctx.drawImage(img, jsonMsg.data[0].areaX, jsonMsg.data[0].areaY)
      }
      img.src = "renders/collected/" + jsonMsg.data[0].fileName;
      break;
    default:
      console.log("no specific functional processing for type " + type);
  }

}

function forHumans(millis) {
  seconds = millis / 1000;
  var levels = [
    [Math.floor(seconds / 31536000), 'years'],
    [Math.floor((seconds % 31536000) / 86400), 'days'],
    [Math.floor(((seconds % 31536000) % 86400) / 3600), 'hours'],
    [Math.floor((((seconds % 31536000) % 86400) % 3600) / 60), 'minutes'],
    [(((seconds % 31536000) % 86400) % 3600) % 60, 'seconds'],
  ];
  var returntext = '';

  for (var i = 0, max = levels.length; i < max; i++) {
    if (levels[i][0] === 0) continue;
    returntext += ' ' + levels[i][0] + ' ' + (levels[i][0] === 1 ? levels[i][1].substr(0, levels[i][1].length - 1) : levels[i][1]);
  };
  return returntext.trim();
}

function upload() {
  var f = document.getElementById('file');
  var fileName = document.getElementById('fileName').value;
  if (f.files.length) {
    var xhr = new XMLHttpRequest();

    console.log("Starting upload of " + fileName);

    xhr.upload.onprogress = function (event) {
      // limit calls to this function
      if (!this.NextSecond) this.NextSecond = 0;
      if ((new Date()).getTime() < this.NextSecond) return;
      this.NextSecond = (new Date()).getTime() + 250;

      data = {
        id: fileName,
        status: Math.round(event.loaded / event.total * 100) + "%"
      }

      dataFormatedForView = {
        elementIds: ["upload-state-table"],
        actions: ["notify", "update-header", "upsert-data"],
        type: "upload",
        data: [data]
      }
      var viewEvent = {
        data: JSON.stringify(dataFormatedForView)
      }
      processSocketMsg(viewEvent);
    };

    xhr.onreadystatechange = function () { // Call a function when the state changes.
      if (this.readyState === XMLHttpRequest.DONE && this.status === 200) {
        data = {
          id: fileName,
          status: "done"
        }

        dataFormatedForView = {
          elementIds: ["upload-state-table"],
          actions: ["notify", "update-header", "upsert-data"],
          type: "upload",
          data: [data]
        }
        var event = {
          data: JSON.stringify(dataFormatedForView)
        }
        processSocketMsg(event);
      }
    }

    xhr.open('post', '/upload/' + fileName, true);
    xhr.setRequestHeader("Content-Type", "application/octet-stream");
    xhr.send(f.files[0]);
  }


  // f.addEventListener('change', processFile, false);


  // function processFile(e) {
  //   var file = f.files[0];
  //   var size = file.size;
  //   var sliceSize = 1024;
  //   var start = 0;

  //   setTimeout(loop, 1);

  //   function loop() {
  //     var end = start + sliceSize;

  //     if (size - end < 0) {
  //       end = size;
  //     }

  //     var s = slice(file, start, end);

  //     send(s, start, end);

  //     if (end < size) {
  //       start += sliceSize;
  //       setTimeout(loop, 1);
  //     }
  //   }
  // }


  // function send(piece, start, end) {
  //   var formdata = new FormData();
  //   var xhr = new XMLHttpRequest();

  //   xhr.open('POST', '/upload/fileName', true);

  //   formdata.append('start', start);
  //   formdata.append('end', end);
  //   formdata.append('file', piece);

  //   xhr.send(formdata);
  // }

  // /**
  //  * Formalize file.slice
  //  */

  // function slice(file, start, end) {
  //   var slice = file.mozSlice ? file.mozSlice :
  //               file.webkitSlice ? file.webkitSlice :
  //               file.slice ? file.slice : noop;

  //   return slice.bind(file)(start, end);
  // }

  // function noop() {

  // }

}