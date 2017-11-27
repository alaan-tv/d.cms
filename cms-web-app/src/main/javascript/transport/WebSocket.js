let socket = new WebSocket("ws://localhost:8080/echo");
socket.onmessage = onMessage;

function onMessage(event) {
    let command = JSON.parse(event.data);
    if( command.action ==="bundle.install") {
        console.info(`Install Bundle ${command.bundle}`);
        bundleContext.installBundle(command.bundle, (bundleContext, exports)=>{
            console.info(`Bundle: ${command.bundle} installed.`);
        });
    } else if( command.action ==="bundle.uninstall") {
        console.info(`Uninstall Bundle ${command.bundle}`);
        bundleContext.removeBundle(command.bundle, ()=>{
            console.info(`Bundle: ${command.bundle} Uninstalled.`);
        });
    } else if( command.action ){
        let event = new CustomEvent(`ws:${command.action}`, {detail: command});
        window.dispatchEvent(event);
    }
}