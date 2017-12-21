import './service/Emitter';
import './service/BundleContext';
import ApplicationInit from './Application';
import {init as transportInit} from "./transport/WebSocket";
import './Externals'
import './service/Shell';
import {request} from "./transport/Request";


transportInit( ()=>{

    /* websocket is ready, client can communicated to server.
    1- get list of essential bundles
    2- for each bundle install get initialization promise of the bundle and add to allInitializationPromise list
    2.2- on allInitializationPromise is resolved run the application
    */
    let allInitializationPromise = [];
    request('components/essential/bundles', {command: 'list'})
        .then( (bundles)=>{
            bundles.forEach( (bundleInfo)=>{

                let promise = new Promise( (resolve, reject)=>{
                    bundleContext.installBundle(bundleInfo, (bundleContext, exports, initializer)=>{
                        if( initializer )
                            initializer.then(resolve, reject);
                        else
                            resolve();
                    });
                } );

                allInitializationPromise.push(promise);
            });
            Promise.all(allInitializationPromise)
                .then( ApplicationInit );
        });
});
