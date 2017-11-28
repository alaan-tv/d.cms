import {Container} from "./components/Container";
import './application.scss';
import React from 'react';



class Application extends Container {
    constructor(props) {
        super(bundleContext, 'd.cms.ui.component.essential', props);
        this.state = {components: []};
    }

    render() {
        return <div className="application">
            {this.state.components}
        </div>;
    }
}

export {Application};