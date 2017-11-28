defineModule(['react', 'components/Container'],(React, Container)=> {

    class SettingsMenuItem extends Container {
        constructor(props) {
            super(props.context, 'd.cms.ui.component.menuItem', props);
        }

        render() {
            return React.createElement(
                'div',
                { className: 'menu-bar' },
                this.state.components
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
                        return React.createElement(SettingsMenuItem, Object.assign({context: context}, props) , null);
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