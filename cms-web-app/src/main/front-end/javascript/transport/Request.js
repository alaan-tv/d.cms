let reqSequence = 0;
let request = (action, ...args) => {
    return new Promise( (resolve, reject) => {
        const seq = reqSequence++;

        globalEmitter.once(`ws:response:data:${seq}`, ({action, response}, ...args)=>{
            resolve(response, ...args);
        });

        globalEmitter.emit(`ws:request`, {
            action: action,
            requestID: seq,
            parameters: args
        });
    });
};


export {request};