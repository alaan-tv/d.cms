import {NavbarBrand, NavbarToggler,} from 'reactstrap';

import Container from '../Container';

class Header extends Container {
    constructor(props) {
        super(bundleContext, 'd.cms.ui.component.NavigationMenuItem', props);
    }

    sidebarToggle(e) {
        e.preventDefault();
        document.body.classList.toggle('sidebar-hidden');
    }

    sidebarMinimize(e) {
        e.preventDefault();
        document.body.classList.toggle('sidebar-minimized');
    }

    mobileSidebarToggle(e) {
        e.preventDefault();
        document.body.classList.toggle('sidebar-mobile-show');
    }

    asideToggle(e) {
        e.preventDefault();
        document.body.classList.toggle('aside-menu-hidden');
    }

    render() {
        return (
            <header className="app-header navbar">
                <NavbarToggler className="d-lg-none" onClick={this.mobileSidebarToggle}>
                    <span className="navbar-toggler-icon"></span>
                </NavbarToggler>
                <NavbarBrand href="#"></NavbarBrand>
                <NavbarToggler className="d-md-down-none mr-auto" onClick={this.sidebarToggle}>
                    <span className="navbar-toggler-icon"></span>
                </NavbarToggler>
                {this.state.components}
                <NavbarToggler className="d-md-down-none" onClick={this.asideToggle}>
                    <span className="navbar-toggler-icon"></span>
                </NavbarToggler>
            </header>
        );
    }
}

export default Header;
