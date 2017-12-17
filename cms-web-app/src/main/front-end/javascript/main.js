import './service/emitter';
import './service/BundleContext';
import ApplicationInit from './Application';
import {init as transportInit} from "./transport/WebSocket";
import './Externals'
import './service/Shell';



transportInit( ()=>{
    ApplicationInit();
});
