import React, {Component} from 'react';
import {CardColumns} from 'reactstrap';
import ComponentPlaceHolder from '../../components/ComponentPlaceHolder';
import {request} from '../../transport/Request';

class Dashboard extends Component {

    constructor(props){
        super(props);
        this.state = {
            components: []
        };
    }

    componentWillMount() {
        request('dashboard:widgets:installed')
            .then( (response) => {
                this.setState({components: response.data});
            })
            .catch( (err) => console.error(`Error fetching [Dashboard] data: ${err}`));
    }

    componentWillUnmount() {
    }

    render() {
        return (
            <div className="animated fadeIn">
                <CardColumns className="cols-2 card-columns">
                    {this.state.components.map( ({cls, SymbolicName, Version, bundle, id, instanceID},idx)=> (
                        <ComponentPlaceHolder key={idx} service='d.cms.ui.component.Dashboard.Card' filter={ {
                            SymbolicName: SymbolicName,
                            Version: Version,
                            id: id
                        } } autoInstallBundle={true} bundle={bundle} instanceID={instanceID} />
                    ) )}
                </CardColumns>
            </div>
        )
    }
}

export default Dashboard;