import {} from './service/BundleContext';
import ApplicationInit from './Application';
import {init as transportInit} from "./transport/WebSocket";
import {} from './Externals'

ApplicationInit();
transportInit();