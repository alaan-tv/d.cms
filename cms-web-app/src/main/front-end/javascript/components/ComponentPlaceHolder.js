import React from 'react';
import {findDOMNode} from 'react-dom';
import PropTypes from 'prop-types';
import {DragSource, DropTarget} from 'react-dnd';
import Container from './Container';
import {BundleContext} from "../service/BundleContext";
import {ItemTypes} from './DragableTypes';

const widgetSource = {
  beginDrag(props) {
    return {
      id: props.id,
      index: props.children.key
    }
  }
};

const widgetTarget = {
  hover(props, monitor, component) {
    props = props.children.props;
    const dragIndex = monitor.getItem().index - 0;
    const hoverIndex = props.index;

    // Don't replace items with themselves
    if (dragIndex === hoverIndex) {
      return
    }

    // Determine rectangle on screen
    const hoverBoundingRect = findDOMNode(component).getBoundingClientRect();

    // Get vertical middle
    const hoverMiddleY = (hoverBoundingRect.bottom - hoverBoundingRect.top) / 2;

    // Determine mouse position
    const clientOffset = monitor.getClientOffset();

    // Get pixels to the top
    const hoverClientY = clientOffset.y - hoverBoundingRect.top;

    // Only perform the move when the mouse has crossed half of the items height
    // When dragging downwards, only move when the cursor is below 50%
    // When dragging upwards, only move when the cursor is above 50%

    // Dragging downwards
    if (dragIndex < hoverIndex && hoverClientY < hoverMiddleY) {
      return
    }

    // Dragging upwards
    if (dragIndex > hoverIndex && hoverClientY > hoverMiddleY) {
      return
    }

    // Time to actually perform the action
    props.moveWidget(dragIndex, hoverIndex);

    // Note: we're mutating the monitor item here!
    // Generally it's better to avoid mutations,
    // but it's good here for the sake of performance
    // to avoid expensive index searches.
    monitor.getItem().index = hoverIndex.toString();
  }
};

@DropTarget(ItemTypes.WIDGET, widgetTarget, connect => ({
  connectDropTarget: connect.dropTarget(),
}))
@DragSource(ItemTypes.WIDGET, widgetSource, (connect, monitor) => ({
  connectDragSource: connect.dragSource(),
  isDragging: monitor.isDragging(),
}))
class DraggableCard extends React.Component {
  render() {
    const {
      isDragging,
      connectDragSource,
      connectDropTarget,
    } = this.props;
    const opacity = isDragging ? 0 : 1;

    return connectDragSource(
      connectDropTarget(
        <div style={{opacity}}>{this.props.children}</div>
      )
    )
  }
}

class ComponentPlaceHolder extends Container {
  constructor(props) {
    let {renderer, context, service, filter: {SymbolicName, Version, id}, bundle, autoInstallBundle, instanceID, moveWidget, index} = props;
    if (!service)
      throw new Error(`ComponentPlaceHolder component requires service attribute.`);
    super(context || bundleContext, service, props);

    this.componentProps = {
      instanceID,
      SymbolicName,
      Version,
      id,
      index,
      moveWidget
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
    return (
      <DraggableCard key={idx}>
        {React.createElement(cls, {key: idx, ...this.componentProps}, null)}
      </DraggableCard>
    );
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

export default ComponentPlaceHolder;
