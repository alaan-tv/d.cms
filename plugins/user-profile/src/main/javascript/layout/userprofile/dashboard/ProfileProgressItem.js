import '../Profile.scss';
import React from 'react';
import {Card, CardBody, CardHeader} from 'reactstrap';
import {Doughnut} from 'react-chartjs-2';
import {request} from "../../../../../../../../cms-web-app/src/main/front-end/javascript/transport/Request";

const data = {
    labels: [
        'Red',
        'Green',
        'Yellow'
    ],
    datasets: []
};

class ProfileProgressItem extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            datasets: []
        }
    }

    componentDidMount() {
        request(`${this.props.SymbolicName}:${this.props.Version}:ProfileProgressItem:request:config`, this.props.instanceID)
            .then( (response) => {
                this.setState({datasets: response.datasets});
            })
            .catch( (err) => console.error(`Error fetching [Dashboard] data: ${err}`));
    }


    render() {
        return <Card>
                <CardHeader>
                    Task Progress {this.props.instanceID}
                    <div className="card-actions">
                        <a href="http://www.chartjs.org">
                            <small className="text-muted">docs</small>
                        </a>
                    </div>
                </CardHeader>
                <CardBody>
                    <div className="chart-wrapper">
                        <Doughnut data={{lables: data.labels, datasets: this.state.datasets}}/>
                    </div>
                </CardBody>
        </Card>;
    }
}

export {ProfileProgressItem};