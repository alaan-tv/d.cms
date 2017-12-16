import React from 'react';
import Container from './Container';
import PropTypes from 'prop-types'
import {BundleContext} from "../service/BundleContext";

class ComponentPlaceHolder extends Container {
    constructor(props) {
        let { context, service, filter: {SymbolicName, Version, id}, bundle, autoInstallBundle, instanceID } = props;
        if (!service)
            throw new Error(`ComponentPlaceHolder component requires service attribute.`);
        super(context || bundleContext, service, props);

        this.componentProps = {
            instanceID, SymbolicName, Version, id
        };

        //auto install bundle
        if( SymbolicName && Version && bundle && id && autoInstallBundle ){
            let bundlePath = `/cms/${SymbolicName}/${Version}/webapp/${bundle}`;
            (bundleContext || bundleContext).installBundle({
                bundlePath: bundlePath,
                SymbolicName: SymbolicName,
                Version: Version
            }, (bundleContext, exports)=>{
                console.info(`%cBundle: ${SymbolicName}-${Version}\nJS Module: ${bundlePath} installed.`, 'color: green;');
            });
        }
    }

    render() {
        return this.state.components.map((c, idx) => React.createElement(c, {key: idx, ...this.componentProps}, null));
    }
}

ComponentPlaceHolder.propTypes = {
    service: PropTypes.string.isRequired,
    filter: PropTypes.oneOfType([
        PropTypes.string,
        PropTypes.shape({})
    ]),
    context: PropTypes.instanceOf(BundleContext),
    autoInstallBundle: PropTypes.bool
};


export default ComponentPlaceHolder;
