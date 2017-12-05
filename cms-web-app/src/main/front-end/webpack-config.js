const webpackCore = require('webpack');
const plugins = [
];
const production = {
    bail: true,
    devtool: 'source-map',
    output: {
        filename: '[name].js'
    },
    module: {
        loaders: [
            {
                test: /.jsx?$/,
                loader: 'babel-loader',
                exclude: /node_modules/,
                query: {
                    babelrc: false,
                    presets: ['react', 'es2015']
                }
            },
            {
                test: /\.scss$/,
                loaders: [ 'style-loader', 'css-loader', 'sass-loader' ]
            }
        ]
    },
    plugins: [
        new webpackCore.DefinePlugin({
            'process.env': {
                NODE_ENV: JSON.stringify('production')
            }
        }),
        new webpackCore.optimize.OccurrenceOrderPlugin(),
        new webpackCore.optimize.UglifyJsPlugin({
            compress: {
                screw_ie8: true, // React doesn't support IE8
                warnings: false
            },
            mangle: {
                screw_ie8: true
            },
            output: {
                comments: false,
                screw_ie8: true
            }
        }),
    ].push(...plugins),
    node: {
        fs: 'empty',
        net: 'empty',
        tls: 'empty'
    }
};

const development = Object.assign(production, {
    plugins: plugins
});
module.exports = {
    production: production,
    development: development
};