import React, {Component} from 'react';
import {Redirect, Route, Switch} from 'react-router-dom';
import {Container} from 'reactstrap';
import Header from '../../components/Header';
import Sidebar from '../../components/Sidebar';
import Breadcrumb from '../../components/Breadcrumb';
import Aside from '../../components/Aside';
import Footer from '../../components/Footer';
import {ServiceTracker} from "../../service/ServiceTracker";

class Full extends Component {
    constructor(props){
        super(props);
        this.state = {
            routers: []
        }
    }

    componentWillMount() {
        this.serviceTracker = new ServiceTracker(
            bundleContext,
            'd.cms.ui.router',
            null,
            (context, serviceReference) => {
                this.setState( (prevState) => {
                    let routers = serviceReference.getService(context);
                    let order = serviceReference.props.order || 'last';
                    switch (order){
                        case 'first':
                            return {
                                routers: [...routers, ...prevState.routers]
                            };
                        case 'last':
                        default:
                            return {
                                routers: [...prevState.routers, ...routers]
                            };
                    }
                })
            },
            (context, serviceReference, routerInfoList) => {
                this.setState( (prevState) => {
                    let routers = prevState.routers.slice();
                    routerInfoList.forEach( (router) => {
                        let indx = routers.indexOf(router);
                        if (indx >= 0)
                            routers.splice(indx, 1);
                    });
                    return {
                        routers: routers
                    };
                });
            }
        )
    }

    render() {
        return (
            <div className="app">
                <Header/>
                <div className="app-body">
                    <Sidebar {...this.props}/>
                    <main className="main">
                        <Breadcrumb/>
                        <Container fluid>
                            <Switch>
                                {this.state.routers.map( (router, indx) =>
                                    <Route key={indx} path={router.path} name={router.name} component={router.component}/>
                                )}
                                <Redirect from="/" to="/dashboard"/>
                            </Switch>
                        </Container>
                    </main>
                    <Aside/>
                </div>
                <Footer/>
            </div>
        );
    }
}

export default Full;
