import React from 'react';
import {ServiceTracker} from "../service/ServiceTracker";
import './application.scss';


class Application extends React.Component {
    constructor(props) {
        super(props);
        this.state = {components: []};
    }

    componentWillMount(){
        this.serviceTracker = new ServiceTracker(bundleContext, 'd.cms.ui.component.essential', {}, this.addingMenu.bind(this), this.removingMenu.bind(this) );
    }

    componentWillUnmount(){
        if( this.serviceTracker ) this.serviceTracker.stop();
    }

    addingMenu(context, serviceReference){
        this.setState((prevState, props) => {
            console.info('prevState is: ', prevState);
            let colst = prevState.components.slice();
            colst.push(serviceReference.getService(bundleContext, {key: colst.length}));
            return {
                components: colst
            };
        });
    }

    removingMenu(context, serviceReference, service){
        this.setState((prevState, props) => {
            console.info('prevState is: ', prevState);
            let colst = prevState.components.slice();
            let indx = colst.indexOf(service);
            if (indx)
                colst.splice(indx,1);
            return {
                components: colst
            };
        });
    }

    render() {
        return <div className="menu-bar">
            {this.state.components}
        </div>;
    }
}

export {Application};