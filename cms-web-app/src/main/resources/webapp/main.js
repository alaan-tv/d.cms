let socket = new WebSocket("ws://localhost:8080/chat");
socket.onmessage = onMessage;

function onMessage(event) {
    let device = JSON.parse(event.data);
    if( device.action ==="greetings")
        console.log("Greetings!");
}

function isFunction(functionToCheck) {
    var getType = {};
    return functionToCheck && getType.toString.call(functionToCheck) === '[object Function]';
}

class BundleContext{
    constructor(context){
        this.context = context;
        this.childContexts = {};
        this.services = {};
        this.bundles = {};
    }

    installBundle(bundlePath, callback){
        requireModule(bundlePath, (module)=>{
            let bundleContext = new BundleContext(this);
            module.activator(bundleContext);
        });
    }

    registerService(cls, instance, bundle, props){
        let registrations = this.services[cls] || [];
        let bundleRegistry = this.bundles[bundle] || [];
        let bundleIndex = bundleRegistry.push({
            instance : instance,
            props: props
        });
        let serviceRegistry = {
            instance : instance,
            bundle : bundle,
            props: props,
            usage: 0
        };
        let serviceIndex = registrations.push(serviceRegistry);
        this.bundles[bundle] = bundleRegistry;
        this.services[cls] = registrations;
        let serviceReference = {
            bundleIndex: bundleIndex,
            serviceIndex: serviceIndex,
            bundle: bundle,
            props: props,
            cls: cls,
            getService: (context, ...args)=> {
                serviceRegistry.usage += 1;
                return isFunction(instance)? instance(context || this, ...args) : instance
            }
        };

        let event = new Event('osgi:service:registered');
        event.serviceReference = serviceReference;
        window.dispatchEvent(event);
        return Object.assign({
            unregister: ()=>{
                delete this.bundles[bundle][bundleIndex];
                delete this.services[cls][serviceIndex];
                let event = new Event('osgi:service:unregistered');
                event.serviceReference = serviceReference;
                window.dispatchEvent(event);
            }
        }, serviceReference);
    }

    getServiceReferences(cls, filter){
        return (this.services[cls] || [])
            .map( (entry, serviceIndex) => {
                return {
                    bundleIndex: null,
                    serviceIndex: serviceIndex,
                    bundle: entry.bundle,
                    props: entry.props,
                    cls: cls,
                    getService: (context, ...args)=> {
                        entry.usage += 1;
                        return isFunction(entry.instance)? instance(context || this, ...args) : entry.instance
                    }
                };
            } )
            .filter( serviceReference => {
                let serviceReference = serviceReferenceEvent.serviceReference;
                let props = serviceReference.props;
                let match = true;
                for( filterKey in filter )
                    match = props.hasOwnProperty(filterKey) && props[filterKey] === filter[filterKey] ;
                return match;
            });
    }
}


class ServiceTracker{
    constructor(cls, filter){
        this.cls = cls;
        this.filter = filter;

        addEventListener('osgi:service:registered', (event)=>{
            let serviceReference = serviceReferenceEvent.serviceReference;
            let props = serviceReference.props;
            let match = true;
            for( filterKey in this.filter ){
                match = props.hasOwnProperty(filterKey) && props[filterKey] === this.filter[filterKey] ;
            }

            if( match )
                this.addingService(bundleContext, serviceReference);
        });

        addEventListener('osgi:service:unregistered', (event)=>{
            let serviceReference = serviceReferenceEvent.serviceReference;
            let props = serviceReference.props;
            let match = true;
            for( filterKey in this.filter ){
                match = props.hasOwnProperty(filterKey) && props[filterKey] === this.filter[filterKey] ;
            }

            if( match )
                this.removingService(bundleContext, serviceReference, nul);
        });
    }

    addingService(bundleContext, serviceReference){

    }
    removingService(bundleContext, serviceReference, service){

    }
}

let bundleContext = new BundleContext();

defineModule("react", ["https://cdnjs.cloudflare.com/ajax/libs/react/16.1.1/umd/react.development.js"], (reactjs) => {
    return reactjs
});
requireModule(["react", "https://cdnjs.cloudflare.com/ajax/libs/react-dom/16.1.1/umd/react-dom.development.js"], (React, ReactDOM)=>{

    window.React = React;

    class NotificationHeaderMenuItem extends React.Component {
        constructor(props) {
            super(props);
            this.state = {components: []};
            // addComponent should return ReactElement, componentFactory creates a custom component and it takes key property value.
            window.addEventListener('osgi:service:registered', (serviceReferenceEvent)=>{
                let serviceReference = serviceReferenceEvent.serviceReference;
                if( serviceReference.cls !== 'd.cms.ui.menuItem' )
                    return;

                this.setState((prevState, props) => {
                    console.info('prevState is: ', prevState);
                    let colst = prevState.components.slice();
                    colst.push({
                        instance: serviceReference.getService(bundleContext, {key: colst.length}),
                        bundle: serviceReference.bundle
                    });
                    return {
                        components: colst
                    };
                });
            });


            window.addEventListener('osgi:service:unregistered', (serviceReferenceEvent)=>{
                let serviceReference = serviceReferenceEvent.serviceReference;
                if( serviceReference.cls !== 'd.cms.ui.menuItem' )
                    return;

                this.setState((prevState, props) => {
                    console.info('prevState is: ', prevState);
                    let colst = prevState.components.slice();
                    let indx = colst.findIndex( (elem)=> { return elem.bundle === serviceReference.bundle } );
                    if (indx )
                        delete colst[indx];
                    return {
                        components: colst
                    };
                });
            });
        }
        render() {
            return React.createElement(
                'ul',
                null,
                this.state.components.map( elem => elem.instance )
            );
        }
    }
    ReactDOM.render(React.createElement(NotificationHeaderMenuItem, null), document.getElementById('root'));


    bundleContext.registerService('d.cms.ui.menuItem', (context, props)=>{
        return React.createElement("h1", props , "React with RequireJS");
    }, 'default', {});

    setInterval(()=>{
        let sr = bundleContext.registerService('d.cms.ui.menuItem', (context, props)=>{
            return React.createElement("h1", props , "New Item By Timer");
        }, 'default', {});

        setTimeout(()=>{
           sr.unregister();
        }, 2000);

    }, 3000);
});