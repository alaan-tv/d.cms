import './Profile.scss';
import React from 'react';
import {Badge, Nav, NavItem, NavLink} from 'reactstrap';
import HeaderDropdown from './HeaderDropdown';


class ProfileMenuItem extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            username: null
        };
    }

    componentWillMount() {
        this.authListener = (event) => {
            this.setState({
                username: event.detail.username
            });
        };
        window.addEventListener('ws:auth:user:authenticated', this.authListener);
    }

    componentWillUnmount() {
        window.removeEventListener('ws:auth:user:authenticated', this.authListener);
    }

    render() {
        return <Nav className="ml-auto" navbar>
            <NavItem className="d-md-down-none">
                <NavLink href="#"><i className="icon-bell"></i><Badge pill color="danger">5</Badge></NavLink>
            </NavItem>
            <NavItem className="d-md-down-none">
                <NavLink href="#"><i className="icon-list"></i></NavLink>
            </NavItem>
            <NavItem className="d-md-down-none">
                <NavLink href="#"><i className="icon-location-pin"></i></NavLink>
            </NavItem>
            <HeaderDropdown/>
        </Nav>;
    }
}

export {ProfileMenuItem};