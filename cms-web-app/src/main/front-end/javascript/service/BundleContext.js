import {isFunction, applyPropsFilter} from "./Utils";

class BundleContext{
    constructor(context, activators, props) {
        this.context = context;
        this.props = props;
        this.childContexts = {};
        this.serviceInstances = {};
        this.activiators = activators || [];

        //if main context then register bundle.install and bundle.uninstall listeners
        if( !context ){
            globalEmitter.addListener('ws:bundle.install', (command)=>{
                let bundles = Array.isArray(command.bundle) ? command.bundle : [command.bundle];
                bundles.forEach( (bundle)=>{
                    bundleContext.installBundle( bundle, (bundleContext, exports)=>{
                        console.info(`%cBundle: ${command.bundle.SymbolicName}-${command.bundle.Version}\nJS Module${command.bundle.bundlePath} installed.`, 'color: blue;');
                    });
                });
            });
            globalEmitter.addListener('ws:bundle.uninstall', (command)=>{
                console.info(`%cUninstall Bundle : ${command.bundle.SymbolicName}-${command.bundle.Version}\nJS Module${command.bundle.bundlePath}`, 'color: red;');
                bundleContext.removeBundle(command.bundle, ()=>{
                    console.info(`%cBundle: ${command.bundle.SymbolicName}-${command.bundle.Version}\nJS Module${command.bundle.bundlePath} uninstalled.`, 'color: red;');
                });
            });
        }
    }

    addServiceListener(event, listener){
        BundleContext.ServiceListeners[event] = BundleContext.ServiceListeners[event] || [];
        BundleContext.ServiceListeners[event].push(listener);
    }

    removeServiceListener(event, listener){
        BundleContext.ServiceListeners[event] = BundleContext.ServiceListeners[event] || [];
        let idx = BundleContext.ServiceListeners[event].indexOf(listener);
        if( idx >= 0 )
            BundleContext.ServiceListeners[event].splice(idx, 1);
    }

    triggerServiceEvent(event, serviceReference){
        BundleContext.ServiceListeners[event] = BundleContext.ServiceListeners[event] || [];
        BundleContext.ServiceListeners[event].forEach( listener => listener.call(listener, serviceReference));
    }

    activate(){
        this.activiators.forEach( activator => activator.start(this) );
    }

    deactivate(){
        this.activiators.forEach( activator => activator.stop(this) );
    }


    installBundle({bundlePath, SymbolicName, Version}, callback){

        requireModule([bundlePath], (module)=>{
            if( BundleContext.Bundles[bundlePath] ){
                let bundle = BundleContext.Bundles[bundlePath];
                callback(bundle.bundleContext, module.exports, module.initializer);
                return;
            }


            let bundleContext = new BundleContext(this, module.activator ? [module.activator] : [] , {SymbolicName: SymbolicName, Version: Version});
            this.childContexts[bundlePath] = bundleContext;
            BundleContext.Bundles[bundlePath] = {
                bundleContext: bundleContext,
                exports: module.exports
            };

            bundleContext.activate();
            callback(bundleContext, module.exports, module.initializer);
        });
    }


    removeBundle({bundlePath}, callback){
        let bundle = BundleContext.Bundles[bundlePath];
        if( bundle ) {
            bundle.bundleContext.deactivate();

            unDefineModule(bundlePath);
            delete this.childContexts[bundlePath];
            delete BundleContext.Bundles[bundlePath];
        }
        callback();
    }


    registerService(cls, instance, props){
        BundleContext.ServiceReferences[cls] = BundleContext.ServiceReferences[cls] || {
            lastIndex: 0
        };
        let serviceProps = Object.assign({cls: cls, ... this.props}, props);

        let serviceReference = {
            context: this,
            props: serviceProps,
            cls: cls,
            instance : instance,
            usage: 0,
            getService: (context, ...args)=> {
                context.serviceInstances[cls] = context.serviceInstances[cls] || {};

                let serviceInstance = context.serviceInstances[cls][serviceReference.serviceIndex];
                if( serviceInstance )
                    return serviceInstance;

                serviceReference.usage += 1;
                serviceInstance = isFunction(instance) ? instance(context || this, ...args) : instance;

                context.serviceInstances[cls][serviceReference.serviceIndex] = serviceInstance;

                return serviceInstance
            },
            applyFilter: (filter)=>{
                return applyPropsFilter(serviceReference.props, filter);
            }
        };

        /**
         * BundleContext.ServiceReferences[dc.menuItem] is dict
         * lastIndex?
         * 0 => ServiceReference
         * 1 => ServiceReference
         */

        let serviceReferenceDict = BundleContext.ServiceReferences[cls];
        serviceReference.serviceIndex = ++serviceReferenceDict.lastIndex;
        serviceReferenceDict[serviceReference.serviceIndex] = serviceReference;
        this.triggerServiceEvent('osgi:service:registered', serviceReference);

        return Object.assign({
            unregister: ()=>{
                delete serviceReferenceDict[serviceReference.serviceIndex];
                this.triggerServiceEvent('osgi:service:unregistered', serviceReference);
            }
        }, serviceReference);
    }

    ungetService(serviceReference){
        let cls = serviceReference.cls;
        let serviceIndex = serviceReference.serviceIndex;
        this.serviceInstances[cls] = this.serviceInstances[cls] || {};

        let serviceInstance = this.serviceInstances[cls][serviceIndex];
        if( serviceInstance ) {
            delete this.serviceInstances[cls][serviceIndex];
            serviceReference.usage -= 1;
        }
    }

    getServiceReferences(cls, filter){
        let serviceReferenceDict = BundleContext.ServiceReferences[cls] || {};
        return Object.keys(serviceReferenceDict)
            .filter( key => key !== 'lastIndex' )
            .map( key =>  serviceReferenceDict[key] )
            .filter( serviceReference => serviceReference.applyFilter(filter) );
    }
}

BundleContext.Bundles = {};
BundleContext.ServiceReferences = {};
BundleContext.ServiceListeners = {};

window.BundleContext = BundleContext;
export {BundleContext};