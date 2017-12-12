import '../Profile.scss';
import React from 'react';
import {Card, CardBody, CardHeader} from 'reactstrap';
import {Pie} from 'react-chartjs-2';

const pieData = {
    labels: [
        'Red',
        'Green',
        'Yellow'
    ],
    datasets: [{
        data: [300, 50, 100],
        backgroundColor: [
            '#FF6384',
            '#36A2EB',
            '#FFCE56'
        ],
        hoverBackgroundColor: [
            '#FF6384',
            '#36A2EB',
            '#FFCE56'
        ]
    }]
};

class ProfilePieItem extends React.Component {
    constructor(props) {
        super(props);
    }


    render() {
        return <Card>
            <CardHeader>
                Acquired Tasks
                <div className="card-actions">
                    <a href="http://www.chartjs.org">
                        <small className="text-muted">docs</small>
                    </a>
                </div>
            </CardHeader>
            <CardBody>
                <div className="chart-wrapper">
                    <Pie data={pieData}/>
                </div>
            </CardBody>
        </Card>;
    }
}

export {ProfilePieItem};