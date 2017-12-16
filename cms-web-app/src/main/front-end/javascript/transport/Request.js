let reqSequence = 0;
let request = (action, ...args) => {
    return new Promise( (resolve, reject) => {
        const seq = reqSequence++;
        const listener = (event)=>{
            resolve(event.detail);
            window.removeEventListener(`ws:response:data:${seq}`,listener);
        };
        window.addEventListener(`ws:response:data:${seq}`, listener);

        const domEvent = new CustomEvent(`ws:request`, {detail: {
            action: action,
            requestID: seq,
            parameters: args
        }});
        window.dispatchEvent(domEvent);
    });
};


export {request};