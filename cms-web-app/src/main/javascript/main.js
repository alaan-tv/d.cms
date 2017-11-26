import {BundleContext} from './service/BundleContext';
import React from 'react';
import ReactDOM from 'react-dom';
import {Application} from './react/Application';

window.React = React;

defineModule("react", [], () => {
    return React;
});

window.bundleContext = new BundleContext();

ReactDOM.render(React.createElement(Application, null), document.getElementById('root'));