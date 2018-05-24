import {BundleContext} from "./BundleContext";
import {isFunction} from "./Utils";
import {request} from '../transport/Request';


globalEmitter.addListener('ws:system.info', (command)=>{
    console.log(`%c[System]%c ${command.SymbolicName}`, 'font-weight: bold', '');
    console.log(`%c[Version]%c ${command.Version}`, 'font-weight: bold', '');
    console.log(`%c[Server-ID]%c name:${command['Server-ID'].hostname} IP:${command['Server-ID'].ip}`, 'font-weight: bold', '');
});


globalEmitter.addListener('ws:console.log', (command)=>{
    console.log(`%c[server]%c ${command.message}`, 'color: blue; font-weight: bold', '');
});


const lb = (filter)=>{
    console.log(`%cSymbolicName\tVersion`, 'background: gray; color: white');
    Object.keys(BundleContext.Bundles).forEach( key => {
        let entry = BundleContext.Bundles[key];
        console.log(`${entry.bundleContext.props.SymbolicName}\t${entry.bundleContext.props.Version}`);
    });
};

const services = (filter)=>{
    Object.keys(BundleContext.ServiceReferences).forEach( key => {
        let serviceReferences = bundleContext.getServiceReferences(key, filter);
        if( !serviceReferences.length )
            return;
        console.log(`%c${key}`, 'background: gray; color: white');
        serviceReferences.forEach(serviceReference =>{
            let serviceType = isFunction(serviceReference.instance) ? 'Factory' : 'Singleton';
            let props = '';
            Object.keys(serviceReference.props).forEach( (key)=> {
                props+= `\n\t\t${key}: ${serviceReference.props[key]}`;
            });
            console.log(`\t${serviceType}
\t${serviceReference.context.props.SymbolicName}-${serviceReference.context.props.Version}
\tusage:${serviceReference.usage}\n\tprops:${props}`
            );
        });
    });
};

const echo = (action, message)=>{
    globalEmitter.emit(`ws:request`, {
        action: 'transport',
        parameters: {
            action, ...message
        }
    });
};



window.shell ={
    lb: lb,
    services: services,
    echo
};