defineModule(['react'],(React)=> {

    class SettingsMenuItem extends React.Component {
        constructor(props) {
            super(props);
        }

        render() {
            return React.createElement(
                'span',
                { className: 'settings-menu-item menu-item' },
                null
            );
        }
    }

    let serviceRegistry = null;

    return {
        activator: {
            start: (context)=>{
                console.info('Menubar Activated');
                serviceRegistry = context.registerService(
                    'd.cms.ui.component.essential',
                    (context, props)=>{
                        return React.createElement(SettingsMenuItem, props , null);
                    },
                    {cateogry: 'Essential Components'}
                );
            },
            stop: (context)=>{
                console.info('Menubar Deactivated');
                if( serviceRegistry ) serviceRegistry.unregister()
            }
        },
        exports:{}
    };

});