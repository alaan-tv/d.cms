import React from 'react';
import Container from './Container';

class ComponentPlaceHolder extends Container {
  constructor(props) {
    if (!props.service)
      throw new Error(`ComponentPlaceHolder component requires service attribute.`);
    super(props.context || bundleContext, props.service, props);
  }

  render() {
    return this.state.components;
  }
}

export default ComponentPlaceHolder;
