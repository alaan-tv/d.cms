import {extendObservable} from 'mobx';
import Login from '../containers/Login';
import {observer} from "mobx-react";
import React from 'react';

const user = extendObservable({}, {
    authenticated: false,
    roles: [],
    permissions: [],
    id: null,
    attrs:{
    }
});


const Authenticated = ()=>{
    return (cls)=>{
        //create stateless component and observe arguments changes to re-render.
        const  ObserverStatelessFunc = observer(
            ({data}) => ( data.authenticated ? React.createElement(cls, {}, null) : <Login/> )
        );
        //return stateless component renders the observer and passing what data to observe.
        return ()=> <ObserverStatelessFunc data={user} />
    }
};


const InRole = (...role)=>{
    return (cls)=>{
        return cls;
    };
};

const HasPermission = (...permissions)=>{
    return (cls)=>{
        return cls;
    };
};


export {user, Authenticated, InRole, HasPermission};