<%--
  Created by IntelliJ IDEA.
  User: anas
  Date: 11/21/17
  Time: 6:34 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Web Socket Plugin</title>
    <style>
        body {
            font-family: Arial, Helvetica, sans-serif;
            font-size: 80%;
            background-color: #1f1f1f;
        }

        #wrapper {
            width: 960px;
            margin: auto;
            text-align: left;
            color: #d9d9d9;
        }

        p {
            text-align: left;
        }

        .button {
            display: inline;
            color: #fff;
            background-color: #f2791d;
            padding: 8px;
            margin: auto;
            border-radius: 8px;
            -moz-border-radius: 8px;
            -webkit-border-radius: 8px;
            box-shadow: none;
            border: none;
        }

        .button:hover {
            background-color: #ffb15e;
        }

        .button a, a:visited, a:hover, a:active {
            color: #fff;
            text-decoration: none;
        }

        #addDevice {
            text-align: center;
            width: 960px;
            margin: auto;
            margin-bottom: 10px;
        }

        #addDeviceForm {
            text-align: left;
            width: 400px;
            margin: auto;
            padding: 10px;
        }

        #addDeviceForm span {
            display: block;
        }

        #content {
            margin: auto;
            width: 960px;
        }

        .device {
            width: 180px;
            height: 110px;
            margin: 10px;
            padding: 16px;
            color: #fff;
            vertical-align: top;
            border-radius: 8px;
            -moz-border-radius: 8px;
            -webkit-border-radius: 8px;
            display: inline-block;
        }

        .device.off {
            background-color: #c8cccf;
        }

        .device span {
            display: block;
        }

        .deviceName {
            text-align: center;
            font-weight: bold;
            margin-bottom: 12px;
        }

        .removeDevice {
            margin-top: 12px;
            text-align: center;
        }

        .device.Appliance {
            background-color: #5eb85e;
        }

        .device.Appliance a:hover {
            color: #a1ed82;
        }

        .device.Electronics {
            background-color: #0f90d1;
        }

        .device.Electronics a:hover {
            color: #4badd1;
        }

        .device.Lights {
            background-color: #c2a00c;
        }

        .device.Lights a:hover {
            color: #fad232;
        }

        .device.Other {
            background-color: #db524d;
        }

        .device.Other a:hover {
            color: #ff907d;
        }

        .device a {
            text-decoration: none;
        }

        .device a:visited, a:active, a:hover {
            color: #fff;
        }

        .device a:hover {
            text-decoration: underline;
        }

    </style>
</head>
<body>

<script type="text/javascript">
    window.onload = init;
    var socket = new WebSocket("ws://localhost:8080/chat");
    socket.onmessage = onMessage;

    function onMessage(event) {
        var device = JSON.parse(event.data);
        if (device.action === "add") {
            printDeviceElement(device);
        }
        if (device.action === "remove") {
            document.getElementById(device.id).remove();
            //device.parentNode.removeChild(device);
        }
        if (device.action === "toggle") {
            var node = document.getElementById(device.id);
            var statusText = node.children[2];
            if (device.status === "On") {
                statusText.innerHTML = "Status: " + device.status + " (<a href=\"#\" OnClick=toggleDevice(" + device.id + ")>Turn off</a>)";
            } else if (device.status === "Off") {
                statusText.innerHTML = "Status: " + device.status + " (<a href=\"#\" OnClick=toggleDevice(" + device.id + ")>Turn on</a>)";
            }
        }
        if( device.action ==="greetings")
            console.log("Greetings!");
    }

    function addDevice(name, type, description) {
        var DeviceAction = {
            action: "add",
            name: name,
            type: type,
            description: description
        };
        socket.send(JSON.stringify(DeviceAction));
    }

    function removeDevice(element) {
        var id = element;
        var DeviceAction = {
            action: "remove",
            id: id
        };
        socket.send(JSON.stringify(DeviceAction));
    }

    function toggleDevice(element) {
        var id = element;
        var DeviceAction = {
            action: "toggle",
            id: id
        };
        socket.send(JSON.stringify(DeviceAction));
    }

    function printDeviceElement(device) {
        var content = document.getElementById("content");

        var deviceDiv = document.createElement("div");
        deviceDiv.setAttribute("id", device.id);
        deviceDiv.setAttribute("class", "device " + device.type);
        content.appendChild(deviceDiv);

        var deviceName = document.createElement("span");
        deviceName.setAttribute("class", "deviceName");
        deviceName.innerHTML = device.name;
        deviceDiv.appendChild(deviceName);

        var deviceType = document.createElement("span");
        deviceType.innerHTML = "<b>Type:</b> " + device.type;
        deviceDiv.appendChild(deviceType);

        var deviceStatus = document.createElement("span");
        if (device.status === "On") {
            deviceStatus.innerHTML = "<b>Status:</b> " + device.status + " (<a href=\"#\" OnClick=toggleDevice(" + device.id + ")>Turn off</a>)";
        } else if (device.status === "Off") {
            deviceStatus.innerHTML = "<b>Status:</b> " + device.status + " (<a href=\"#\" OnClick=toggleDevice(" + device.id + ")>Turn on</a>)";
            //deviceDiv.setAttribute("class", "device off");
        }
        deviceDiv.appendChild(deviceStatus);

        var deviceDescription = document.createElement("span");
        deviceDescription.innerHTML = "<b>Comments:</b> " + device.description;
        deviceDiv.appendChild(deviceDescription);

        var removeDevice = document.createElement("span");
        removeDevice.setAttribute("class", "removeDevice");
        removeDevice.innerHTML = "<a href=\"#\" OnClick=removeDevice(" + device.id + ")>Remove device</a>";
        deviceDiv.appendChild(removeDevice);
    }

    function showForm() {
        document.getElementById("addDeviceForm").style.display = '';
    }

    function hideForm() {
        document.getElementById("addDeviceForm").style.display = "none";
    }

    function formSubmit() {
        var form = document.getElementById("addDeviceForm");
        var name = form.elements["device_name"].value;
        var type = form.elements["device_type"].value;
        var description = form.elements["device_description"].value;
        hideForm();
        document.getElementById("addDeviceForm").reset();
        addDevice(name, type, description);
    }

    function init() {
        hideForm();
    }
</script>

<div id="wrapper">
    <h1>Java Websocket Home</h1>
    <p>Welcome to the Java WebSocket Home. Click the Add a device button to start adding devices.</p>
    <br/>
    <div id="addDevice">
        <div class="button"><a href="#" OnClick="showForm()">Add a device</a></div>
        <form id="addDeviceForm">
            <h3>Add a new device</h3>
            <span>Name: <input type="text" name="device_name" id="device_name"></span>
            <span>Type:
                        <select id="device_type">
                            <option name="type" value="Appliance">Appliance</option>
                            <option name="type" value="Electronics">Electronics</option>
                            <option name="type" value="Lights">Lights</option>
                            <option name="type" value="Other">Other</option>
                        </select></span>

            <span>Description:<br/>
                        <textarea name="description" id="device_description" rows="2" cols="50"></textarea>
                    </span>

            <input type="button" class="button" value="Add" onclick=formSubmit()>
            <input type="reset" class="button" value="Cancel" onclick=hideForm()>
        </form>
    </div>
    <br/>
    <h3>Currently connected devices:</h3>
    <div id="content">
    </div>
</div>
</body>
</html>
