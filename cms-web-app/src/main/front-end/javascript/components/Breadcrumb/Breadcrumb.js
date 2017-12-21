import React from 'react';
import {Link} from 'react-router-dom';
import {Breadcrumb, BreadcrumbItem} from 'reactstrap';
import {observer} from "mobx-react";

const BreadcrumbsItem = ({name, url, last}) => {
    return (
        last ?
            (
                <BreadcrumbItem active>{name}</BreadcrumbItem>
            ) :
            (
                <BreadcrumbItem>
                    <Link to={url || ''}>
                        {name}
                    </Link>
                </BreadcrumbItem>
            )
    );
};

const Breadcrumbs = observer( ({breadcrumb}) => {
    let path = [{
        name: 'Home',
        url: '/'
    }, ...breadcrumb.path];
    const items = path.map((path, i) => <BreadcrumbsItem key={i} name={path.name} url={path.url} last={breadcrumb.path.length === i}/>);
    return (
        <Breadcrumb>
            {items}
        </Breadcrumb>
    );
} );

export default Breadcrumbs;
