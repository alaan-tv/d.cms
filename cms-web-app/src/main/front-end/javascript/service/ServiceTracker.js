
class ServiceTracker{
    constructor(context, cls, filter, addingService, removingService){
        this.context = context;
        this.cls = cls;

        const filterAsString = (filter) => {
            return typeof filter === 'string' ? filter :
                Object.keys(filter).map( key => {
                    let value = filter[key];
                    if( typeof value === 'string')
                        return `${key} = "${value.replace(/"/g, '\\"')}"`;
                    return `${key} = ${filter[key]}`;
                } ).reduce( (accumulator, currentValue) => `${accumulator} and ${currentValue}`)
        };

        this.filter = `cls="${cls}" ${filter ? ' and ' + filterAsString(filter) : ''}`;
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

        this.context.getServiceReferences(cls, this.filter)
            .forEach( (serviceReference => this.addingService(this.context, serviceReference) ))
    }

    stop(){
        this.context.removeServiceListener('osgi:service:registered', this.serviceRegistered);
        this.context.removeServiceListener('osgi:service:unregistered', this.serviceUnRegistered );
    }
}


export {ServiceTracker}