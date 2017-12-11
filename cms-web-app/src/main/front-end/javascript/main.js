import {BundleContext} from './service/BundleContext';
import React from 'react';
import ReactDOM from 'react-dom';
import ApplicationInit from './Application';
import {init as transportInit} from "./transport/WebSocket";
import {Container} from "./components/Container";


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

ApplicationInit();
transportInit();