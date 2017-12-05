
let socket = new WebSocket(`ws://${document.location.host}/cms`);
socket.onmessage = (event) =>{
    let command = JSON.parse(event.data);
    let domEvent = new CustomEvent(`ws:${command.action}`, {detail: command});
    window.dispatchEvent(domEvent);
};