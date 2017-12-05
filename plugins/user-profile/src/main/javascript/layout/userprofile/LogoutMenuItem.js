import './Profile.scss';
import React from 'react';

class LogoutMenuItem extends React.Component {
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
        return <div className="menu-item">
            <span className="logout-menu-item">&nbsp;</span>
            {this.state.username ? <span>Logout{this.state.username}</span> : <span>Anonymous User</span>}
        </div>;
    }
}

export {LogoutMenuItem};