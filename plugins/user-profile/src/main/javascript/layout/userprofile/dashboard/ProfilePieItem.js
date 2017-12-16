import '../Profile.scss';
import React from 'react';
import {Card, CardBody, CardHeader} from 'reactstrap';
import {Pie} from 'react-chartjs-2';
import {request} from "../../../../../../../../cms-web-app/src/main/front-end/javascript/transport/Request";

const data = {
    labels: [
        'Red',
        'Green',
        'Yellow'
    ],
    datasets: []
};

class ProfilePieItem extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            datasets: []
        }
    }

    componentDidMount() {
        request(`component/${this.props.id}`, {instanceID: this.props.instanceID, command: 'getData'})
            .then( (response) => {
                this.setState({datasets: response.response.datasets});
            })
            .catch( (err) => console.error(`Error fetching [Dashboard] data: ${err}`));
    }


    render() {
        return <Card>
            <CardHeader>
                Acquired Tasks {this.props.instanceID}
                <div className="card-actions">
                    <a href="http://www.chartjs.org">
                        <small className="text-muted">docs</small>
                    </a>
                </div>
            </CardHeader>
            <CardBody>
                <div className="chart-wrapper">
                    <Pie data={{lables: data.labels, datasets: this.state.datasets}}/>
                </div>
            </CardBody>
        </Card>;
    }
}

export {ProfilePieItem};