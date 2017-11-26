defineModule(()=> {

    class Menubar extends React.Component {
        constructor(props) {
            super(props);
        }

        render() {
            return React.createElement(
                'h1',
                null,
                "Menu Item by Module"
            );
        }
    }

    let serviceRegistry = null;

    return {
        activator: {
            start: (context)=>{
                console.info('Menubar Activated');
                serviceRegistry = context.registerService('d.cms.ui.component.essential', (context, props)=>{
                    return React.createElement(Menubar, props , null);
                }, 'default', {});
            },
            stop: (context)=>{
                console.info('Menubar Deactivated');
                if( serviceRegistry ) serviceRegistry.unregister()
            }
        },
        exports:{}
    };

});