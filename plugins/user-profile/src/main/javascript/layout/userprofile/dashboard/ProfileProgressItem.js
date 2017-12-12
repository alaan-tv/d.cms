import '../Profile.scss';
import React from 'react';
import {Card, CardBody, CardHeader} from 'reactstrap';
import {Doughnut} from 'react-chartjs-2';

const data = {
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

class ProfileProgressItem extends React.Component {
    constructor(props) {
        super(props);
    }


    render() {
        return <Card>
                <CardHeader>
                    Task Progress
                    <div className="card-actions">
                        <a href="http://www.chartjs.org">
                            <small className="text-muted">docs</small>
                        </a>
                    </div>
                </CardHeader>
                <CardBody>
                    <div className="chart-wrapper">
                        <Doughnut data={data}/>
                    </div>
                </CardBody>
        </Card>;
    }
}

export {ProfileProgressItem};