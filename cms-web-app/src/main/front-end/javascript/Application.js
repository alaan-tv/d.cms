import ReactDOM from 'react-dom';
import {BrowserRouter, Route, Switch} from 'react-router-dom';

// Styles
// Import Font Awesome Icons Set
import 'font-awesome/css/font-awesome.min.css';
// Import Simple Line Icons Set
import 'simple-line-icons/css/simple-line-icons.css';
// Import Main styles for this application
import '../scss/style.scss'
// Temp fixes for other css libraries
import '../scss/core/_override.scss'
// Style files for React-Grid-Layout
import '../node_modules/react-grid-layout/css/styles.css'
import '../node_modules/react-resizable/css/styles.css'

// Containers
import Full from './containers/Full';


export default () => {

    ReactDOM.render((
        <BrowserRouter basename="/cms">
            <Switch>
                <Route path="/" name="Home" component={Full}/>
            </Switch>
        </BrowserRouter>
    ), document.getElementById('root'));

}
