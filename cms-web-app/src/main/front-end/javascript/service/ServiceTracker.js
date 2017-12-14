import {BundleContext} from "./BundleContext";

class ServiceTracker{
    constructor(context, cls, filter, addingService, removingService){
        this.context = context;
        this.cls = cls;
        this.filter = `cls="${cls}" ${filter ? 'and' + filter: ''}`;
        this.addingService = addingService;
        this.removingService = removingService;

        this.serviceRegistered = (serviceReference)=>{
            if( serviceReference.applyFilter(this.filter) )
                this.addingService(this.context, serviceReference);
        };
        this.serviceUnRegistered = (serviceReference)=>{
            if( serviceReference.applyFilter(this.filter) ) {
                this.removingService(bundleContext, serviceReference, serviceReference.getService(this.context));
                context.ungetService(serviceReference);
            }
        };

        this.context.addServiceListener('osgi:service:registered', this.serviceRegistered);
        this.context.addServiceListener('osgi:service:unregistered',  this.serviceUnRegistered );

        this.context.getServiceReferences(cls)
            .forEach( (serviceReference => this.addingService(this.context, serviceReference) ))
    }

    stop(){
        this.context.removeServiceListener('osgi:service:registered', this.serviceRegistered);
        this.context.removeServiceListener('osgi:service:unregistered', this.serviceUnRegistered );
    }
}


export {ServiceTracker}