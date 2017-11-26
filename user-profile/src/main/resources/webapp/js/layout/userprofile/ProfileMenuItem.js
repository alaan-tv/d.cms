defineModule(()=> {

    class ProfileMenuItem extends React.Component {
        constructor(props) {
            super(props);
        }

        render() {
            return React.createElement(
                'h1',
                null,
                "User Profile"
            );
        }
    }

    let serviceRegistry = null;

    return {
        activator: {
            start: (context)=>{
                console.info('ProfileMenuItem Activated');
                serviceRegistry = context.registerService('d.cms.ui.component.essential', (context, props)=>{
                    return React.createElement(ProfileMenuItem, props , null);
                }, 'default', {});
            },
            stop: (context)=>{
                console.info('ProfileMenuItem Deactivated');
                if( serviceRegistry ) serviceRegistry.unregister()
            }
        },
        exports:{}
    };

});