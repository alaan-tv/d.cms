import './Profile.scss';

let React = requireModule('react');

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
        return <div className="menu-item">
            <span className="user-profile-menu-item">&nbsp;</span>
            {this.state.username ? <span>{this.state.username} Profile</span> : <span>Profile!</span>}
        </div>;
    }
}

export {ProfileMenuItem};