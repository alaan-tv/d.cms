import {BundleContext} from './service/BundleContext';
import React from 'react';
import ReactDOM from 'react-dom';
import {init} from './Application';
import {init as transportInit} from "./transport/WebSocket";


window.React = React; //export as global variable
window.ReactDOM = ReactDOM; //export as global variable
window.bundleContext = new BundleContext();

defineModule("react", [], () => {
    return React;
});

defineModule("react-dom", [], () => {
    return ReactDOM;
});

defineModule('bundleContext', [], ()=>{
    return window.bundleContext;
});

init();
transportInit();