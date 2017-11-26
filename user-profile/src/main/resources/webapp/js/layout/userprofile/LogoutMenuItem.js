defineModule(()=> {

    class LogoutMenuItem extends React.Component {
        constructor(props) {
            super(props);
        }

        render() {
            return React.createElement(
                'h1',
                null,
                "Logout"
            );
        }
    }

    let serviceRegistry = null;

    return {
        activator: {
            start: (context)=>{
                console.info('LogoutMenuItem Activated');
                serviceRegistry = context.registerService('d.cms.ui.component.essential', (context, props)=>{
                    return React.createElement(LogoutMenuItem, props , null);
                }, 'default', {});
            },
            stop: (context)=>{
                console.info('LogoutMenuItem Deactivated');
                if( serviceRegistry ) serviceRegistry.unregister()
            }
        },
        exports:{}
    };

});