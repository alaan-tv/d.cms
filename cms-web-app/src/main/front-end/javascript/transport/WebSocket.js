function init(callback) {
    let socket = new WebSocket(`ws://${document.location.host}/cms/ws`);
    socket.onmessage = (event) =>{
        let command = JSON.parse(event.data);

        globalEmitter.emit(`ws:${command.action}`, command);
    };

    globalEmitter.addListener(`ws:request`, (...args)=>{
        args.forEach( arg => socket.send(JSON.stringify(arg)) );
    });

    socket.onopen = callback;

    socket.onclose = ()=>{
      setTimeout(init, 1000);
    };

    window.onclose = (event)=>{
        socket.close(1, 'window closed');
    }
}

export {init};