import {Component} from 'react';
import {Responsive, WidthProvider} from 'react-grid-layout';

const ResponsiveReactGridLayout = WidthProvider(Responsive);

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
      components: [],
      layouts: {
        lg: [
          {x: 0, y: 0, w: 2, h: 5},
          {x: 2, y: 0, w: 1, h: 5},
          {x: 0, y: 6, w: 3, h: 5}
        ],
        sm: [
          {x: 0, y: 0, w: 1, h: 3},
          {x: 0, y: 3, w: 1, h: 3},
          {x: 0, y: 6, w: 1, h: 3}
        ]
      }
    };
  }

  componentWillMount() {
    Request.request(`component/dashboard`, {instanceID: 0})
      .then( (data) => {
        this.setState({components: data});
      })
      .catch( (err) => console.error(`Error fetching [Dashboard] data: ${err}`));
  }

  onLayoutChange(layout, layouts) {
    this.setState({layouts});
  }

  render() {
    return (
      <div className="animated fadeIn">
        <h1>Dashboard</h1>
        <ResponsiveReactGridLayout
          className="layout"
          cols={{lg: 3, sm: 1}}
          breakpoints={{lg: 480, sm: 0}}
          rowHeight={50}
          layouts={this.state.layouts}
          onLayoutChange={(layout, layouts) => this.onLayoutChange(layout, layouts)}
        >
          {this.state.components.map(({cls, SymbolicName, Version, bundle, id, instanceID}, idx) => (
            <div key={idx}>
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
          ))}
        </ResponsiveReactGridLayout>
      </div>
    )
  }
}
