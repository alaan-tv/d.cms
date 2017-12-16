import {ProfileMenuItem} from './ProfileMenuItem';
import {ProfileProgressItem} from './dashboard/ProfileProgressItem';
import {ProfilePieItem} from './dashboard/ProfilePieItem';

defineModule(['react'], (React)=> {

    let serviceRegistry = [];

    return {
        activator: {
            start: (context)=>{
                console.info('User-Profile Components Activated');

                serviceRegistry.push(
                    context.registerService('d.cms.ui.component.NavigationMenuItem', (context, props)=>{
                        return React.createElement(ProfileMenuItem, props , null);
                    }, {id: '5d4b2f67-ee47-4a84-947d-d9b65d94e3ab'})
                );

                serviceRegistry.push(
                    context.registerService('d.cms.ui.component.Dashboard.Card', (context, props)=>{
                        return ProfilePieItem;
                    }, {id: '3ecbd060-dd59-4d9a-a2cc-ca41f1562a4a'})
                );

                serviceRegistry.push(
                    context.registerService('d.cms.ui.component.Dashboard.Card', (context, props)=>{
                        return ProfileProgressItem;
                    }, {
                        id: '5d4b2f67-ee47-4a84-947d-d9b65d94e3ab',
                        roles: ['editor-in-chief', 'system-admin', 'site-manager']
                    })
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