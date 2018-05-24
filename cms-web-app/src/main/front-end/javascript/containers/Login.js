import React, {Component} from 'react';
import {user} from '../service/Auth'

export default class Login extends Component {
    constructor(props){
        super(props);
    }

    handleLogin(e){
        e.preventDefault();
        user.authenticated = true;
        user.id = Math.random();
        user.roles = ['admin'];
        user.permissions = ['read', 'write', 'delete', 'list'];
        user.attrs = {
            firstName: 'Tester',
            lastName: 'GodSpeed'
        };
    }

    componentWillMount() {
    }

    render() {
        return (
            <div className="app">
                <main className="main">
                    Login Form is here
                    <button onClick={this.handleLogin}>Login</button>
                </main>
            </div>
        );
    }
}
