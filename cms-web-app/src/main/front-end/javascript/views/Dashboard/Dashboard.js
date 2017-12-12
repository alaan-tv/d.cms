import React from 'react';
import {CardColumns} from 'reactstrap';
import Container from "../../components/Container";


class Dashboard extends Container {

    constructor(props){
        super(bundleContext,'d.cms.ui.component.Dashboard.Card', props);
    }

    render() {
        return (
            <div className="animated fadeIn">
                <CardColumns className="cols-2 card-columns">
                    {this.state.components}
                </CardColumns>
            </div>
        )
    }
}

export default Dashboard;
