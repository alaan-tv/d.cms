import * as React from 'react';
import * as ReactDOM from 'react-dom';
import * as ChartJS from 'chart.js';
import * as ReactChartJS from 'react-chartjs-2';
import * as ReactRouter from 'react-router-dom';
import * as ReactStrap from 'reactstrap';
import * as Mobx from 'mobx';
import * as MobxReact from 'mobx-react';
import * as ReactGridLayout from 'react-grid-layout';
import * as Request from './transport/Request';
import ComponentPlaceHolder from './components/ComponentPlaceHolder';

//export as global variable
window.React = React;
window.ReactDOM = ReactDOM;
window.ChartJS = ChartJS;
window.ReactChartJS = ReactChartJS;
window.ReactRouter = ReactRouter;
window.ReactStrap = ReactStrap;
window.bundleContext = new BundleContext(null, null, {SymbolicName: 'ADMIN', Version: '1.0'});
window.Request = Request;
window.Mobx = Mobx;
window.MobxReact = MobxReact;
window.ComponentPlaceHolder = ComponentPlaceHolder;
window.ReactGridLayout = ReactGridLayout;

defineModule("react", [], () => {
  return React;
});

defineModule("react-dom", [], () => {
  return ReactDOM;
});

defineModule('bundleContext', [], ()=>{
  return window.bundleContext;
});

export {React, ReactDOM, ChartJS, ReactChartJS, ReactRouter, ReactStrap};
