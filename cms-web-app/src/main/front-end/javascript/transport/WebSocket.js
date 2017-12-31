function init(callback) {
    let socket = new WebSocket(`ws://${document.location.host}/cms`);
    socket.onmessage = (event) =>{
        let command = JSON.parse(event.data);

        globalEmitter.emit(`ws:${command.action}`, command);
    };

    globalEmitter.addListener(`ws:request`, (...args)=>{
        args.forEach( arg => socket.send(JSON.stringify(arg)) );
    });

    socket.onopen = callback;

    window.onclose = (event)=>{
        socket.close(1, 'window closed');
    }
}

export {init};