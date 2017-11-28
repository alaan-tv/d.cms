import {LogoutMenuItem} from './LogoutMenuItem';
import {ProfileMenuItem} from './ProfileMenuItem';

defineModule(['react'], (React)=> {

    let serviceRegistry = [];

    return {
        activator: {
            start: (context)=>{
                console.info('User-Profile Components Activated');

                serviceRegistry.push(
                    context.registerService('d.cms.ui.component.essential', (context, props)=>{
                        return React.createElement(LogoutMenuItem, props , null);
                    }, {service: 'userprofile.logout.menuitem'})
                );

                serviceRegistry.push(
                    context.registerService('d.cms.ui.component.essential', (context, props)=>{
                        return React.createElement(ProfileMenuItem, props , null);
                    }, {service: 'userprofile.menuitem'})
                );
            },
            stop: (context)=>{
                console.info('User-Profile Components Deactivated');
                serviceRegistry.forEach( r => r.unregister() );
            }
        },
        exports:{}
    };

});