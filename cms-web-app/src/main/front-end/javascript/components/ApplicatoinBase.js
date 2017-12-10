import React from 'react';
import UniversalRouter from 'universal-router';
import routes from '../routes';
import history from '../core/history';

class ApplicationBase extends React.Component {
    constructor(props) {
        super(props);
        this.state = {component: null};
    }

    static context = {
        insertCss: (...styles) => {
            const removeCss = styles.map(style => style._insertCss()); // eslint-disable-line no-underscore-dangle, max-len
            return () => {
                removeCss.forEach(f => f());
            };
        },
        setTitle: value => (document.title = value),
        setMeta: (name, content) => {
            // Remove and create a new <meta /> tag in order to make it work
            // with bookmarks in Safari
            const elements = document.getElementsByTagName('meta');
            Array.from(elements).forEach((element) => {
                if (element.getAttribute('name') === name) {
                    element.parentNode.removeChild(element);
                }
            });
            const meta = document.createElement('meta');
            meta.setAttribute('name', name);
            meta.setAttribute('content', content);
            document
                .getElementsByTagName('head')[0]
                .appendChild(meta);
        },
    };

    componentWillMount(){
        let currentLocation = history.location;
        const router = new UniversalRouter(routes, {
            context: {
                render: (a) => a,
                context: ApplicationBase.context
            },
        });


        // Re-render the app when window.location changes
        function onLocationChange(location) {
            currentLocation = location;

            router.resolve(
                location.pathname
            ).then((component) => {
                this.setState({component: component});
            }).catch(err => console.error(err)); // eslint-disable-line no-console
        }

        // Add History API listener and trigger initial change
        const removeHistoryListener = history.listen(onLocationChange.bind(this));
        history.push(currentLocation, {init: 'yes'});
    }

    render(){
        return <div>{ this.state.component ? this.state.component : 'Loading...'}</div>
    }
}

export {ApplicationBase};