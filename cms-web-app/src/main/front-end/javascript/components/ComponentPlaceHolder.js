import React from 'react';
import PropTypes from 'prop-types';
import Container from './Container';
import {BundleContext} from "../service/BundleContext";

export default class ComponentPlaceHolder extends Container {
  constructor(props) {
    let {renderer, context, service, filter: {SymbolicName, Version, id}, bundle, autoInstallBundle, instanceID} = props;
    if (!service)
      throw new Error(`ComponentPlaceHolder component requires service attribute.`);
    super(context || bundleContext, service, props);

    this.componentProps = {
      instanceID,
      SymbolicName,
      Version,
      id,
    };

    this.renderer = renderer || this.defaultRenderer.bind(this);

    //auto install bundle
    if (SymbolicName && Version && bundle && id && autoInstallBundle) {
      let bundlePath = `/cms/${SymbolicName}/${Version}/webapp/${bundle}`;
      (bundleContext || bundleContext).installBundle({
        bundlePath: bundlePath,
        SymbolicName: SymbolicName,
        Version: Version
      }, (bundleContext, exports) => {
        //console.info(`%cBundle: ${SymbolicName}-${Version}\nJS Module: ${bundlePath} installed.`, 'color: green;');
      });
    }
  }

  defaultRenderer(cls, idx) {
    return (React.createElement(cls, {key: idx, ...this.componentProps}, null));
  }

  render() {
    return this.state.components.map(this.renderer);
  }
}

ComponentPlaceHolder.propTypes = {
  service: PropTypes.string.isRequired,
  filter: PropTypes.oneOfType([
    PropTypes.string,
    PropTypes.shape({})
  ]),
  context: PropTypes.instanceOf(BundleContext),
  autoInstallBundle: PropTypes.bool,
  renderer: PropTypes.func
};
