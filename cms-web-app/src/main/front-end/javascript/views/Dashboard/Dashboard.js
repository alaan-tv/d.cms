import {Component} from 'react';
import {CardColumns} from 'reactstrap';
import {DragDropContext, Droppable, Draggable} from 'react-beautiful-dnd';

// a little function to help us with reordering the result
const reorder = (list, startIndex, endIndex) => {
  const result = Array.from(list);
  const [removed] = result.splice(startIndex, 1);
  result.splice(endIndex, 0, removed);

  return result;
};

// using some little inline style helpers to make the app look okay
const grid = 8;
const getItemStyle = (isDragging, draggableStyle) => ({
  // some basic styles to make the items look a bit nicer
  userSelect: 'none',
  outline: isDragging ? '1px solid green' : 'none',
  margin: `0 0 ${grid}px 0`,

  // change background colour if dragging
  background: isDragging ? 'lightgreen' : 'grey',

  // styles we need to apply on draggables
  ...draggableStyle,
});
const getListStyle = isDraggingOver => ({
  background: isDraggingOver ? 'lightblue' : 'lightgrey',
  padding: grid,
  width: 250,
});

export default class Dashboard extends Component {

  constructor(props){
    super(props);
    this.state = {
      /**
       * @components: list of dict, dict is:
       * 1. cls: class identifies service interface
       * 2. SymbolicName: the symbolic name of the backend (OSGI) bundle which provides the service
       * 3. Version: the version of the backend (OSGI) bundle which provides the service
       * 4. bundle: the javascript bundle provided by the backend bundle
       * 5. id: the identifier of the widget
       * 6. instanceID: the identifier of the widget instance
       */
      components: []
    };
    this.onDragEnd = this.onDragEnd.bind(this);
  }

  componentWillMount() {
    Request.request(`component/dashboard`, {instanceID: 0})
      .then( (data) => {
        this.setState({components: data});
      })
      .catch( (err) => console.error(`Error fetching [Dashboard] data: ${err}`));
  }

  onDragEnd(result) {
    if (!result.destination) {
      return;
    }

    const components = reorder(
      this.state.components,
      result.source.index,
      result.destination.index
    );

    this.setState({
      components,
    });
  }

  render() {
    return (
      <DragDropContext onDragEnd={this.onDragEnd}>
        <h1>Dashboard</h1>
        <Droppable droppableId="droppable">
          {(provided, snapshot) => (
            <div ref={provided.innerRef}
                 style={getListStyle(snapshot.isDraggingOver)}
            >
              {this.state.components.map(({cls, SymbolicName, Version, bundle, id, instanceID}, index) => (
                <Draggable key={id + instanceID} draggableId={id + instanceID} index={index}>
                  {(provided, snapshot) => (
                    <div>
                      <div ref={provided.innerRef}
                           {...provided.draggableProps}
                           {...provided.dragHandleProps}
                           style={getItemStyle(
                             snapshot.isDragging,
                             provided.draggableProps.style,
                           )}>
                        <ComponentPlaceHolder
                          service='d.cms.ui.component.Dashboard.Card'
                          bundle={bundle}
                          autoInstallBundle={true}
                          instanceID={instanceID}
                          filter={{
                            SymbolicName: SymbolicName,
                            Version: Version,
                            id: id
                          }}
                        />
                      </div>
                      {provided.placeholder}
                    </div>
                  )}
                </Draggable>
              ))}
              {provided.placeholder}
            </div>
          )}
        </Droppable>
      </DragDropContext>
    )
  }
}
