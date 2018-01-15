import {Component} from 'react';
import {CardColumns} from 'reactstrap';
import update from 'immutability-helper';

export default class Dashboard extends Component {

  constructor(props){
    super(props);
    this.moveWidget = this.moveWidget.bind(this);
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
  }

  componentWillMount() {
    Request.request(`component/dashboard`, {instanceID: 0})
      .then( (data) => {
        this.setState({components: data});
      })
      .catch( (err) => console.error(`Error fetching [Dashboard] data: ${err}`));
  }

  moveWidget(dragIndex, hoverIndex) {
    const {components} = this.state;
    const dragWidget = components[dragIndex];

    this.setState(
      update(this.state, {
        components: {
          $splice: [[dragIndex, 1], [hoverIndex, 0, dragWidget]],
        },
      }),
    )
  }

  render() {
    return (
      <div className="animated fadeIn">
        <h1>Dashboard</h1>
        <CardColumns className="cols-2 card-columns">
          {this.state.components.map(({cls, SymbolicName, Version, bundle, id, instanceID}, idx) => (
            <ComponentPlaceHolder
              key={idx}
              index={idx}
              service='d.cms.ui.component.Dashboard.Card'
              bundle={bundle}
              autoInstallBundle={true}
              instanceID={instanceID}
              filter={{
                SymbolicName: SymbolicName,
                Version: Version,
                id: id
              }}
              moveWidget={this.moveWidget}/>
          ))}
        </CardColumns>
      </div>
    )
  }
}
