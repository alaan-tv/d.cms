import {BundleContext} from './service/BundleContext';
import React from 'react';
import ReactDOM from 'react-dom';
import {Application} from './Application';


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

ReactDOM.render(React.createElement(Application, null), document.getElementById('root'));