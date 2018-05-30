let trial = 0;
function reconnect() {
    setTimeout(() => {
        init(() => {} );
    }, 1000);
}

globalEmitter.addListener(`ws:connection:open`, ()=>{
    let timer = setInterval(()=>{
        globalEmitter.emit(`ws:request`, {action: 'ws.heartbeat'});
    }, 2000);

    globalEmitter.addListener(`ws:connection:closed`, ()=>{
        clearInterval(timer);
    });

});

function init(callback) {
    let socket = new WebSocket(`ws://${document.location.host}/cms/ws`);
    socket.onmessage = (event) => {
        let command = JSON.parse(event.data);
        globalEmitter.emit(`ws:${command.action}`, command);
    };

    let subscription = globalEmitter.addListener(`ws:request`, (...args) => {
        args.forEach(arg => socket.send(JSON.stringify(arg)));
    });

    socket.onopen = () => {
        if(trial === 0 )
            globalEmitter.emit(`ws:connection:connected`, {});
        else
            globalEmitter.emit(`ws:connection:reconnected`, {});
        globalEmitter.emit(`ws:connection:open`, {});
        trial = 0;
        callback();
    };

    socket.onclose = () => {
        subscription.remove();
        if( trial === 0 ) {
            globalEmitter.emit(`ws:connection:closed`, {});
        }
        trial += 1;
        reconnect();
    };

    window.onclose = (event) => {
        socket.close(1, 'window closed');
    }
}

globalEmitter.addListener(`ws:connection:closed`, ()=>
    console.log(`%c[Websocket]%c Connectivity: %c[LOST]`, 'font-weight: bold;', '', 'color: red')
);
globalEmitter.addListener(`ws:connection:connected`, ()=>
    console.log(`%c[Websocket]%c Connectivity: %c[OK]`, 'font-weight: bold;', '', 'color: green')
);
globalEmitter.addListener(`ws:connection:reconnected`, ()=>
    console.log(`%c[Websocket]%c Connectivity: %c[RECONNECTED]`, 'font-weight: bold;', '', 'color: green')
);

export {init};