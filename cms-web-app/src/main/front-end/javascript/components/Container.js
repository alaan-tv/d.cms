import {Component} from 'react';
import {ServiceTracker} from '../service/ServiceTracker';

class Container extends Component {
  constructor(bundleContext, service, props) {
    super(props);
    this.bundleContext = bundleContext;
    this.service = service;
    this.state = {components: []};
  }

  componentWillMount() {
    this.serviceTracker = new ServiceTracker(this.bundleContext, this.service, this.props.filter, this.addingMenu.bind(this), this.removingMenu.bind(this));
  }

  componentWillUnmount() {
    if (this.serviceTracker) this.serviceTracker.stop();
  }

  addingMenu(context, serviceReference) {
    this.setState((prevState, props) => {
      let colst = prevState.components.slice();
      colst.push(serviceReference.getService(context, {key: colst.length, ...this.props}));
      return {
        components: colst
      };
    });
  }

  removingMenu(context, serviceReference, service){
    this.setState((prevState, props) => {
      let colst = prevState.components.slice();
      let indx = colst.indexOf(service);
      if (indx >= 0)
        colst.splice(indx,1);
      return {
        components: colst
      };
    });
  }
}

// Define the container module to be used by plugins.
defineModule('components/Container', [], () => {
  return Container;
});

export default Container;
