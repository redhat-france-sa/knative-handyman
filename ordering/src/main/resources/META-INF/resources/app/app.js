// global on load
/*
global.listeners.uiconfig.push(
  async function (uiconfig) {
    console.log('custom operation on load ' + JSON.stringify(uiconfig));
  }
)
*/

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
      if (this.readyState === XMLHttpRequest.DONE && this.status === 202) {
        data = {
          id: fileName,
          status: "done"
        }
        dataFormatedForView = {
          elementIds: ["upload-state-table"],
          actions: ["update-header", "upsert-data"],
          type: "upload",
          data: [data]
        }
        var event = {
          data: JSON.stringify(dataFormatedForView)
        }
        processSocketMsg(event);
        console.log("Get rendering options for " + this.responseText);
        appContext.fileObject = JSON.parse(this.responseText);
        getRenderingOptions(this.responseText);
      }
    }
    xhr.open('post', '/upload/' + fileName, true);
    xhr.setRequestHeader("Content-Type", "application/octet-stream");
    xhr.send(f.files[0]);
  }
}

function getRenderingOptions(uploadedFile) {
  var xhr = new XMLHttpRequest();
  xhr.open('post', '/order/options', true);
  xhr.setRequestHeader('Content-type', 'application/json');
  xhr.onreadystatechange = function() {
    if (this.readyState == XMLHttpRequest.DONE && this.status == 200) {
      console.log("Received options: " + this.responseText);
      appContext.options = JSON.parse(this.responseText);
      refreshRenderingOptions();
    }
  };
  xhr.send(uploadedFile);
}

function refreshRenderingOptions() {
   var options = document.getElementById("rendering-options");

   var newOptions = '';
   appContext.options.forEach(option => {
     newOptions += '<div id="option-' + option.name
        + '" class="rendering-option" '
        + 'onclick="chooseOption(\''+option.name+'\')">';
     newOptions += '<h1>' + option.name + '</h1>';
     newOptions += '<p>' + option.cost + ' euros</p>';
     newOptions += '</div>';
   });
   options.innerHTML = newOptions;
}

function chooseOption(name) {
  console.log("Picked option " + name);

  var option = appContext.options.filter(o => o.name === name)[0];
  console.log("Found valid option: " + JSON.stringify(option));

  var c = document.getElementById("image");
  c.width = option.resolutionX;
  c.height = option.resolutionY;

  var data = {fileObject: appContext.fileObject, option: option};
  var xhr = new XMLHttpRequest();
  xhr.open('post', '/order/rendering', true);
  xhr.setRequestHeader('Content-type', 'application/json');
  xhr.onreadystatechange = function() {
    if (this.readyState == XMLHttpRequest.DONE && this.status == 200) {
      console.log("Received response: " + this.responseText);
    }
  };
  xhr.send(JSON.stringify(data));
}