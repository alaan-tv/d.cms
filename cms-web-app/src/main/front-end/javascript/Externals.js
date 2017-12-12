import * as React from 'react';
import * as ReactDOM from 'react-dom';
import * as ChartJS from 'chart.js';
import * as ReactChartJS from 'react-chartjs-2';
import * as ReactRouter from 'react-router-dom';
import * as ReactStrap from 'reactstrap';


window.React = React; //export as global variable
window.ReactDOM = ReactDOM; //export as global variable
window.ChartJS = ChartJS;
window.ReactChartJS = ReactChartJS;
window.ReactRouter = ReactRouter;
window.ReactStrap = ReactStrap;
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

export {React, ReactDOM, ChartJS, ReactChartJS, ReactRouter, ReactStrap}