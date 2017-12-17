const webpack = require('webpack');
const path = require('path');

function config(DEBUG, VERBOSE) {

    const config = {
        context: path.resolve(__dirname, './javascript'),

        output: {
            sourcePrefix: '  ',
        },

        module: {
            rules: [
                {
                    test: /\.(js|jsx)$/,
                    exclude: /node_modules/,
                    use: {
                        loader: 'babel-loader',
                        options: {
                            cacheDirectory: DEBUG,
                            presets: ['react', 'env'],
                            plugins: ['transform-object-rest-spread']
                        }
                    }
                },
                {
                    test: /\.scss$/,
                    loaders: [ 'style-loader', 'css-loader', 'sass-loader' ]
                },
                {
                    test: /\.css$/,
                    loaders: [ 'style-loader', 'css-loader' ]
                }
            ]
        },
        externals: {
            'react': 'React',
            'react-dom': 'ReactDOM',
            'chart.js': 'ChartJS',
            'react-chartjs-2': 'ReactChartJS',
            'react-router-dom': 'ReactRouter',
            'reactstrap': 'ReactStrap',
            '../../../../../../../../cms-web-app/src/main/front-end/javascript/transport/Request': 'Request'
        }
    };

    let PLUGINS = [
        new webpack.HotModuleReplacementPlugin(),
        new webpack.NamedModulesPlugin(),
        // Assign the module and chunk ids by occurrence count
        // Consistent ordering of modules required if using any hashing ([hash] or [chunkhash])
        // https://webpack.github.io/docs/list-of-plugins.html#occurrenceorderplugin
        new webpack.optimize.OccurrenceOrderPlugin(true),
    ];

    if( !DEBUG )
        PLUGINS.push(
            // Minimize all JavaScript output of chunks
            // https://github.com/mishoo/UglifyJS2#compressor-options
            new webpack.optimize.UglifyJsPlugin({
                compress: {
                    screw_ie8: true, // jscs:ignore requireCamelCaseOrUpperCaseIdentifiers
                    warnings: VERBOSE,
                },
            }),

            // A plugin for a more aggressive chunk merging strategy
            // https://webpack.github.io/docs/list-of-plugins.html#aggressivemergingplugin
            new webpack.optimize.AggressiveMergingPlugin()
        );

    const clientConfig = Object.assign(config, {

        output: {
            filename: DEBUG ? 'userprofile.js' : 'userprofile.js',
            chunkFilename: DEBUG ? '[name].[id].js?[chunkhash]' : '[name].[id].[chunkhash].js',
            publicPath: '/cms/fe/'
        },

        target: 'web',

        plugins: PLUGINS,

        // Choose a developer tool to enhance debugging
        // http://webpack.github.io/docs/configuration.html#devtool
        devtool: DEBUG ? 'source-map' : false,
    });


    return clientConfig;
}

module.exports = {
    production: config(false, false),
    development: config(true, true)
};
