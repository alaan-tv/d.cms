import ReactDOM from 'react-dom';
import React, {Component} from 'react';
import {BrowserRouter, Route, Switch} from 'react-router-dom';
import {Authenticated} from "./service/Auth";
import Login from './containers/Login';

// Styles
// Import Font Awesome Icons Set
import 'font-awesome/css/font-awesome.min.css';
// Import Simple Line Icons Set
import 'simple-line-icons/css/simple-line-icons.css';
// Import Main styles for this application
import '../scss/style.scss'
// Temp fixes for other css libraries
import '../scss/core/_override.scss'

// Containers
import Full from './containers/Full';

@Authenticated()
class ApplicationShell extends Component{
    render(){
        return <BrowserRouter basename="/cms">
            <Switch>
                <Route path="/" name="Home" component={Full}/>
            </Switch>
        </BrowserRouter>;
    }
}

export default () => {

    ReactDOM.render((
        <ApplicationShell />
    ), document.getElementById('root'));

}
