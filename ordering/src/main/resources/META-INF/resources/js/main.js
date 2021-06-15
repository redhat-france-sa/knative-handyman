var appContext = {};

// GLOBAL VARS with objservers
var globalVar = {
  listeners: {  //map of list of listener functions on change
    uiconfig: [
      async function (uiconfig) {
        loadTheme(uiconfig);
      },
    ],
    pageReady: [
      async function (pageReady) {
        console.log("HTML ready, connecting to WebSocket...");
        connectSocket();
      }
    ]
  }
}

var global = new Proxy(globalVar, {
  set: function (target, key, value) {
    if (key !== 'listeners') {
      target[key] = value;
      target.listeners[key].forEach(fn => fn(target[key])); //executing list of functions
    }
  }
});

async function renderHtml() {
  includeHTML();
  global.pageReady = true;
}

function includeHTML() {
  var z, i, elmnt, file, xhttp;
  /* Loop through a collection of all HTML elements: */
  z = document.getElementsByTagName("*");
  for (i = 0; i < z.length; i++) {
    elmnt = z[i];
    /*search for elements with a certain atrribute:*/
    file = elmnt.getAttribute("w3-include-html");
    if (file) {
      /* Make an HTTP request using the attribute value as the file name: */
      xhttp = new XMLHttpRequest();
      xhttp.onreadystatechange = function () {
        if (this.readyState == 4) {
          if (this.status == 200) { elmnt.innerHTML = this.responseText; }
          if (this.status == 404) { elmnt.innerHTML = "Page not found."; }
          /* Remove the attribute, and call this function once more: */
          elmnt.removeAttribute("w3-include-html");
          includeHTML();
        }
      }
      xhttp.open("GET", file, true);
      xhttp.send();
      /* Exit the function: */
      return;
    }
  }
}

async function loadConfig() {
  /*
  response = await fetch(window.location.protocol + "//" + window.location.host + '/uiconfig', {
    method: 'get',
    credentials: 'include'
  });
  global.uiconfig = await response.json();
  */
}

function loadTheme(config) {
  var head = document.getElementsByTagName('HEAD')[0];
  var link = document.createElement('link');
  link.rel = 'stylesheet';
  link.type = 'text/css';
  link.href = 'css/' + config.theme + '.css';
  head.appendChild(link);
}

function backEndRequest(elementId, path, meth, msg) {
  if (path.startsWith("${")) {
    path = "/" + path;
  }

  fetch(window.location.protocol + "//" + window.location.host + path, {
    method: meth,
    credentials: 'include',
    body: msg
  })
    .then(response => response.text())
    .then(function (response) {
      if (elementId != null) {
        element = document.getElementById(elementId)
        element.innerHTML = response;
        flashAnimateRef(element);
      }
    })
}

function formDataToJson(formId) {
  var jsonData = {};
  var form = document.getElementById(formId)
  for (var i = 0; i < form.elements.length; i++) {
    var e = form.elements[i];
    jsonData[e.id] = e.value;
  }
  return JSON.stringify(jsonData);
}

function toggleResponsiveMenu() {
  var x = document.getElementById("nav");
  if (x.classList.contains("responsive")) {
    x.classList.remove("responsive");
  } else {
    x.classList.add("responsive");
  }
}

function connectSocket() {
  // receiving functions
  server = ((window.location.protocol === 'https:') ? 'wss://' : 'ws://') + window.location.hostname + ":" + window.location.port + "/websocket";
  socket = new WebSocket(server);
  socket.onmessage = processSocketMsg;

  socket.onopen = function () {
    logo = document.getElementById("logo");
    if (logo != null) {
      logo.classList.remove("negative");
      document.getElementById("status").classList.add("hidden");
    }
  }
  socket.onclose = function (e) {
    console.log('Socket is closed. Reconnect will be attempted in 2 second.', e.reason);
    logo = document.getElementById("logo");
    if (logo != null) {
      logo.classList.add("negative");
      document.getElementById("status").classList.remove("hidden");
    }
    setTimeout(function () {
      connectSocket();
    }, 2000);
  };

  socket.onerror = function (err) {
    console.error('Socket encountered error: ', err.message, 'Closing socket');
    socket.close();
  };
}

function processSocketMsg(event) {
  text = event.data;
  console.log("Receiving msg: " + text);
  var jsonMsg;
  try {
    jsonMsg = JSON.parse(text);
  } catch (e) {
    return;
  }

  if (jsonMsg.type != null && jsonMsg.type === "upload") {
    jsonMsg.formatting = {
      status: function (value){
        if (value=="done"){
          return "positive";
        } else {
          return "negative";
        }
      }
    }
  }

  if (jsonMsg.actions != null) {
    if (jsonMsg.actions.includes("update-header")) {
      updateTableHeaders(jsonMsg);
    }
    if (jsonMsg.actions.includes("upsert-data")) {
      insertOrUpdateTable(jsonMsg);
    }
  }

  if (jsonMsg.status != null) {
    // This is a status update.
    var statusMsg;
    switch (jsonMsg.status) {
      case "received":
        statusMsg = "Received request";
        break;
      case "opened":
        statusMsg = "Opened file";
        break;
      case "starting":
        statusMsg = "Starting rendering...";
        break;
      case "rendered":
        statusMsg = "Finished rendering";
        break;
    }
    insertStatusMessage(jsonMsg, statusMsg);
  } else if (jsonMsg.result != null) {
    // This is a result response.
    console.log("Processing a partial result: " + jsonMsg.result);
    var ctx = document.getElementById("image").getContext("2d");
    var img = new Image();
    img.onload = function () {
      ctx.drawImage(img, jsonMsg.areaX, jsonMsg.areaY)
    }
    img.src = jsonMsg.result;
  }
};

function insertStatusMessage(jsonMsg, statusMsg) {
  var table = document.getElementById("rendering-status-table");
  var rowContent = '<tr>';
  rowContent += '<td>' + jsonMsg.timestamp + '</td>';
  rowContent += '<td>' + jsonMsg.areaX + ':' + jsonMsg.areaY + '</td>';
  rowContent += '<td>' + statusMsg + '</td></tr>';

  var row = table.insertRow(-1);
  row.id = 'status-' + jsonMsg.timestamp;
  row.innerHTML = rowContent;
  flashAnimate("status-" + jsonMsg.timestamp);
}

function clearTable(tableId){
  document.getElementById(tableId).innerHTML = "";
}

function insertOrUpdateTable(rawData){
  for (var j = 0; j < rawData.elementIds.length; j++) {
    tableId = rawData.elementIds[j];

    var table = document.getElementById(tableId);
    // loop through the data
    for (var i = 0; i < rawData.data.length; i++) {

      row = table.rows.namedItem(rawData.data[i].id);
      if (row == null) {
        row = table.insertRow(-1);
        row.id =rawData.data[i].id;
      }
      for (const key in rawData.data[i]) {
        if (rawData.data[i].hasOwnProperty(key)) {
          const element = rawData.data[i][key];
          cell = row.cells.namedItem(key);
          if (cell == null){
              cell = row.insertCell(-1);
              cell.id = key;
          }

          if (element!=null){
            if(typeof element == "object"){
                cell.innerHTML=JSON.stringify(element,undefined,2);
            } else {
              if (cell.innerHTML != element) {
                cell.innerHTML=element;

                //  conditional formatting
                if ( rawData.formatting !== undefined && rawData.formatting.hasOwnProperty(key) )  {
                  cell.className = "";
                  cell.classList.add(rawData.formatting[key](element));
                 }

                flashAnimateRef(cell);
              }
            }
          }
        }
      }
    }
  }
}

// expects array of elements with flat key value
function updateTableHeaders(rawData){
  for (var i = 0; i < rawData.elementIds.length; i++) {
    tableId = rawData.elementIds[i];

    line = "<th>"+Object.keys(rawData.data[0]).join("</th><th>")+"</th>";
    row = document.getElementById(tableId + "-header");
    // header does not exist
    if (row == null) {
      row = document.getElementById(tableId).insertRow(0);
      row.id = tableId + "-header";
    }
    row.innerHTML=line;
  }
}

function flashAnimate(elementId) {
  element = document.getElementById(elementId);
  flashAnimateRef(element);
}

function flashAnimateRef(element) {
  element.classList.remove("flash-animation");
  void element.offsetWidth;
  element.classList.add("flash-animation");
}