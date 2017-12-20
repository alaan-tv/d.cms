import React, {Component} from 'react';
import {NavLink} from 'react-router-dom';
import {Badge, Nav, NavItem, NavLink as RsNavLink} from 'reactstrap';
import classNames from 'classnames';
import nav from './_nav';
import SidebarFooter from './../SidebarFooter';
import SidebarForm from './../SidebarForm';
import SidebarHeader from './../SidebarHeader';
import SidebarMinimizer from './../SidebarMinimizer';
import {ServiceTracker} from "../../service/ServiceTracker";
import {observer} from "mobx-react";


// badge addon to NavItem
const NavigationBadge = observer( ({badge}) => {
    if (badge) {
        const classes = classNames(badge.class);
        return <Badge className={classes} color={badge.variant}>{badge.text}</Badge>;
    }
    return [];
} );

class Sidebar extends Component {

    constructor(props){
        super(props);
        this.state = {
          routers: []
        };
    }

    componentWillMount(){
        this.serviceTracker = new ServiceTracker(
            bundleContext,
            'd.cms.ui.router',
            null,
            (context, serviceReference)=>{
                this.setState((prevState, props) => {
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
                });
            },
            (context, serviceReference, routerInfoList)=>{
                this.setState((prevState, props) => {
                    let routers = prevState.routers.slice();
                    routerInfoList.forEach( (routerInfo)=>{
                        let indx = routers.indexOf(routerInfo);
                        if (indx >= 0)
                            routers.splice(indx,1);
                    });
                    return {
                        routers: routers
                    };
                });
            }
        );
    }

    handleClick(e) {
        e.preventDefault();
        e.target.parentElement.classList.toggle('open');
    }

    activeRoute(routeName, props) {
        // return this.props.location.pathname.indexOf(routeName) > -1 ? 'nav-item nav-dropdown open' : 'nav-item nav-dropdown';
        return props.location.pathname.indexOf(routeName) > -1 ? 'nav-item nav-dropdown open' : 'nav-item nav-dropdown';

    }

    // todo Sidebar nav secondLevel
    // secondLevelActive(routeName) {
    //   return this.props.location.pathname.indexOf(routeName) > -1 ? "nav nav-second-level collapse in" : "nav nav-second-level collapse";
    // }


    render() {

        const props = this.props;
        const activeRoute = this.activeRoute;
        const handleClick = this.handleClick;

        // simple wrapper for nav-title item
        const wrapper = item => {
            return (item.wrapper && item.wrapper.element ? (React.createElement(item.wrapper.element, item.wrapper.attributes, item.name)) : item.name)
        };

        // nav list section title
        const title = (title, key) => {
            const classes = classNames('nav-title', title.class);
            return (<li key={key} className={classes}>{wrapper(title)} </li>);
        };

        // nav list divider
        const divider = (divider, key) => {
            const classes = classNames('divider', divider.class);
            return (<li key={key} className={classes}></li>);
        };

        // nav item with nav link
        const navItem = (item, key) => {
            const classes = {
                item: classNames(item.class),
                link: classNames('nav-link', item.variant ? `nav-link-${item.variant}` : ''),
                icon: classNames(item.icon)
            };
            return (
                navLink(item, key, classes)
            )
        };

        // nav link
        const navLink = (item, key, classes) => {
            const url = item.url ? item.url : '';
            return (
                <NavItem key={key} className={classes.item}>
                    {isExternal(url) ?
                        <RsNavLink href={url} className={classes.link} active>
                            <i className={classes.icon}></i>{item.name}<NavigationBadge badge={item.badge} />
                        </RsNavLink>
                        :
                        <NavLink to={url} className={classes.link} activeClassName="active">
                            <i className={classes.icon}></i>{item.name}<NavigationBadge badge={item.badge} />
                        </NavLink>
                    }
                </NavItem>
            )
        };

        // nav dropdown
        const navDropdown = (item, key) => {
            return (
                <li key={key} className={activeRoute(item.url, props)}>
                    <a className="nav-link nav-dropdown-toggle" href="#" onClick={handleClick.bind(this)}><i
                        className={item.icon}></i>{item.name}</a>
                    <ul className="nav-dropdown-items">
                        {navList(item.children)}
                    </ul>
                </li>)
        };

        // nav type
        const navType = (item, idx) =>
            item.title ? title(item, idx) :
                item.divider ? divider(item, idx) :
                    item.children ? navDropdown(item, idx)
                        : navItem(item, idx);

        // nav list
        const navList = (items) => {
            return items.map((item, index) => navType(item, index));
        };

        const isExternal = (url) => {
            const link = url ? url.substring(0, 4) : '';
            return link === 'http';
        };

        // sidebar-nav root
        return (
            <div className="sidebar">
                <SidebarHeader/>
                <SidebarForm/>
                <nav className="sidebar-nav">
                    <Nav>
                        {navList(this.state.routers)}
                    </Nav>
                </nav>
                <SidebarFooter/>
                <SidebarMinimizer/>
            </div>
        )
    }
}

export default Sidebar;
