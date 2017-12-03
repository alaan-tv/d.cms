import './Profile.scss';
import React from 'react';
const ReactDOM = require('react-dom');


class ProfileMenuItem extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            username: null
        };
    }

    componentWillMount(){
        this.authListener = (event) => {
            this.setState({
                username: event.detail.username
            });
        };
        window.addEventListener('ws:auth:user:authenticated', this.authListener);
    }

    componentWillUnmount(){
        window.removeEventListener('ws:auth:user:authenticated', this.authListener);
    }

    render() {
        return  <ButtonGroup className="menu-item">
            <DropdownButton id="dropdown-btn-menu" bsStyle="success" title="Dropdown">
                <MenuItem key="1">Dropdown link</MenuItem>
                <MenuItem key="2">Dropdown link</MenuItem>
            </DropdownButton>
            <Button bsStyle="info">Middle</Button>
            <Button bsStyle="info">Right</Button>
        </ButtonGroup>;
    }
}

export {ProfileMenuItem};