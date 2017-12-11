import React, {Component} from 'react';
import {Doughnut, Pie} from 'react-chartjs-2';
import {Card, CardBody, CardHeader, CardColumns} from 'reactstrap';

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

class Dashboard extends Component {

    render() {
        return (
            <div className="animated fadeIn">
                <CardColumns className="cols-2 card-columns">
                    <Card>
                        <CardHeader>
                            Doughnut Chart
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
                    </Card>
                    <Card>
                        <CardHeader>
                            Pie Chart
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
                    </Card>
                    <Card>
                        <CardHeader>
                            Doughnut Chart
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
                    </Card>
                    <Card>
                        <CardHeader>
                            Pie Chart
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
                    </Card>
                    <Card>
                        <CardHeader>
                            Doughnut Chart
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
                    </Card>
                    <Card>
                        <CardHeader>
                            Pie Chart
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
                    </Card>
                </CardColumns>
            </div>
        )
    }
}

export default Dashboard;
