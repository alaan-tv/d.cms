function init(callback) {
    let socket = new WebSocket(`ws://${document.location.host}/cms`);
    socket.onmessage = (event) =>{
        let command = JSON.parse(event.data);
        let domEvent = new CustomEvent(`ws:${command.action}`, {detail: command});
        window.dispatchEvent(domEvent);
    };

    window.addEventListener(`ws:request`, (event)=>{
        socket.send(JSON.stringify(event.detail));
    });

    socket.onopen = callback;
}

export {init};