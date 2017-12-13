import {BundleContext} from "./BundleContext";
import {isFunction} from "./utils";


window.addEventListener('ws:system.info', (event)=>{
    let command = event.detail;
    console.log(`%cSystem: ${command.SymbolicName}-${command.Version}`, 'color: red; font-size: 1.5em;');
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
        console.log(`${key}`);
        let entry = BundleContext.ServiceReferences[key];
        Object.keys(entry).forEach(indx =>{
            if(indx !== 'lastIndex') {
                let serviceReference = entry[indx];
                let serviceType = isFunction(serviceReference.instance) ? 'Factory' : 'Singleton';
                console.log(`%c\t${serviceType}\t${serviceReference.context.props.SymbolicName}-${serviceReference.context.props.Version}\tusage:${serviceReference.usage}`, 'background: gray; color: white');
            }
        });
    });
};


window.shell ={
    lb: lb,
    services: services
};